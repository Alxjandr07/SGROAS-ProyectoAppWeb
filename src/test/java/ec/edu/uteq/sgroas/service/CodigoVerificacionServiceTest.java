package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.entity.VerificationCode;
import ec.edu.uteq.sgroas.repository.VerificationCodeRepository;
import ec.edu.uteq.sgroas.service.VerificationCodeService.Tipo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodigoVerificacionServiceTest {

    private static final String EMAIL = "carlos@sgroas.com";

    @Mock
    private VerificationCodeRepository repository;

    @InjectMocks
    private VerificationCodeService service;

    private String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private VerificationCode registro(String codigoClaro) {
        return VerificationCode.builder()
                .id(1L)
                .email(EMAIL)
                .codigoHash(sha256(codigoClaro))
                .tipo(Tipo.VERIFICACION.name())
                .expiraEn(Instant.now().plusSeconds(600))
                .intentos(0)
                .usado(false)
                .creadoEn(Instant.now())
                .build();
    }

    @Test
    void generarDebeInvalidarAnterioresYGuardarNuevo() {
        when(repository.save(any(VerificationCode.class))).thenAnswer(inv -> inv.getArgument(0));

        String codigo = service.generate(EMAIL, Tipo.VERIFICACION);

        verify(repository).deleteByEmailAndTipo(EMAIL, Tipo.VERIFICACION.name());
        verify(repository).save(argThat(reg ->
                reg.getCodigoHash().equals(sha256(codigo))
                        && reg.getEmail().equals(EMAIL)
                        && reg.getTipo().equals(Tipo.VERIFICACION.name())
                        && !reg.isUsado()));
        assertTrue(codigo.matches("\\d{6}"));
    }

    @Test
    void puedeReenviarSinRegistroDebeSerTrue() {
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.empty());

        assertTrue(service.canResend(EMAIL, Tipo.VERIFICACION));
    }

    @Test
    void puedeReenviarConCodigoRecienteDebeSerFalse() {
        VerificationCode reciente = registro("123456");
        reciente.setCreadoEn(Instant.now().minusSeconds(10));
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(reciente));

        assertFalse(service.canResend(EMAIL, Tipo.VERIFICACION));
    }

    @Test
    void puedeReenviarConCodigoAntiguoDebeSerTrue() {
        VerificationCode antiguo = registro("123456");
        antiguo.setCreadoEn(Instant.now().minusSeconds(120));
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(antiguo));

        assertTrue(service.canResend(EMAIL, Tipo.VERIFICACION));
    }

    @Test
    void validarCodigoCorrectoDebeMarcarloUsado() {
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(registro("123456")));

        service.validate(EMAIL, Tipo.VERIFICACION, "123456");

        verify(repository).save(argThat(VerificationCode::isUsado));
    }

    @Test
    void validarCodigoIncorrectoDebeIncrementarIntentosYFallar() {
        VerificationCode reg = registro("123456");
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(reg));

        assertThrows(IllegalArgumentException.class,
                () -> service.validate(EMAIL, Tipo.VERIFICACION, "999999"));
        verify(repository).save(argThat(c -> c.getIntentos() == 1 && !c.isUsado()));
    }

    @Test
    void validarSinRegistroDebeFallar() {
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.validate(EMAIL, Tipo.VERIFICACION, "123456"));
    }

    @Test
    void validarCodigoYaUsadoDebeFallar() {
        VerificationCode usado = registro("123456");
        usado.setUsado(true);
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(usado));

        assertThrows(IllegalArgumentException.class,
                () -> service.validate(EMAIL, Tipo.VERIFICACION, "123456"));
    }

    @Test
    void validarCodigoExpiradoDebeFallar() {
        VerificationCode expirado = registro("123456");
        expirado.setExpiraEn(Instant.now().minusSeconds(60));
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(expirado));

        assertThrows(IllegalArgumentException.class,
                () -> service.validate(EMAIL, Tipo.VERIFICACION, "123456"));
    }

    @Test
    void validarConIntentosAgotadosDebeFallar() {
        VerificationCode agotado = registro("123456");
        agotado.setIntentos(5);
        when(repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(
                EMAIL, Tipo.VERIFICACION.name())).thenReturn(Optional.of(agotado));

        assertThrows(IllegalArgumentException.class,
                () -> service.validate(EMAIL, Tipo.VERIFICACION, "123456"));
    }
}