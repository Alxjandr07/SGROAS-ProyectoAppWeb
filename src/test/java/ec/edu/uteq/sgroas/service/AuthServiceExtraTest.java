package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.AuthResponse;
import ec.edu.uteq.sgroas.dto.LoginRequest;
import ec.edu.uteq.sgroas.dto.RefreshTokenRequest;
import ec.edu.uteq.sgroas.entity.Role;
import ec.edu.uteq.sgroas.entity.User;
import ec.edu.uteq.sgroas.exception.UnverifiedEmailException;
import ec.edu.uteq.sgroas.repository.UserRepository;
import ec.edu.uteq.sgroas.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceExtraTest {

    @Mock
    private UserRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private VerificationCodeService codigoVerificacionService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void configurarRefreshExpiration() {
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);
    }

    private User usuarioEjemplo() {
        return User.builder()
                .id(1L)
                .name("Administrador SGROAS")
                .email("admin@sgroas.com")
                .passwordHash("password-encriptado")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private void simularGeneracionTokens(User usuario) {
        when(jwtService.generateToken(usuario)).thenReturn("access-token-prueba");
        when(tokenService.createRefreshToken(eq("admin@sgroas.com"), eq(604800000L)))
                .thenReturn("refresh-token-prueba");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
    }

    @Test
    void verificarEmailCorrectoDebeActivarCuentaYRetornarTokens() {
        User usuario = usuarioEjemplo();
        usuario.setActive(false);
        usuario.setVerified(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        simularGeneracionTokens(usuario);

        AuthResponse response = authService.verifyEmail("admin@sgroas.com", "654321");

        assertEquals("access-token-prueba", response.accessToken());
        verify(codigoVerificacionService).validate("admin@sgroas.com",
                VerificationCodeService.Type.VERIFICACION, "654321");
        verify(usuarioRepository).save(argThat(u ->
                Boolean.TRUE.equals(u.getActive()) && Boolean.TRUE.equals(u.getVerified())));
    }

    @Test
    void loginConCorreoNoVerificadoDebeLanzarExcepcion() {
        User sinVerificar = usuarioEjemplo();
        sinVerificar.setActive(false);
        sinVerificar.setVerified(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(sinVerificar));
        when(passwordEncoder.matches("123456", "password-encriptado")).thenReturn(true);

        assertThrows(UnverifiedEmailException.class,
                () -> authService.login(new LoginRequest("admin@sgroas.com", "123456")));
    }

    @Test
    void restablecerContrasenaDebeActualizarClave() {
        User usuario = usuarioEjemplo();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nueva-clave-1")).thenReturn("hash-nuevo");

        authService.resetPassword("admin@sgroas.com", "111222", "nueva-clave-1");

        verify(codigoVerificacionService).validate("admin@sgroas.com",
                VerificationCodeService.Type.RESET_PASSWORD, "111222");
        verify(usuarioRepository).save(argThat(u -> "hash-nuevo".equals(u.getPasswordHash())));
    }

    @Test
    void reenviarCodigoDebeGenerarYEnviarNuevoCodigo() {
        User sinVerificar = usuarioEjemplo();
        sinVerificar.setVerified(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(sinVerificar));
        when(codigoVerificacionService.canResend("admin@sgroas.com",
                VerificationCodeService.Type.VERIFICACION)).thenReturn(true);
        when(codigoVerificacionService.generate("admin@sgroas.com",
                VerificationCodeService.Type.VERIFICACION)).thenReturn("999888");

        authService.resendVerificationCode("admin@sgroas.com");

        verify(emailService).sendVerificationCode(
                "admin@sgroas.com", "Administrador SGROAS", "999888");
    }

    @Test
    void reenviarCodigoConCuentaVerificadaNoDebeEnviarNada() {
        User verificado = usuarioEjemplo();
        verificado.setVerified(true);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(verificado));

        authService.resendVerificationCode("admin@sgroas.com");

        verifyNoInteractions(emailService);
    }

    @Test
    void solicitarRestablecimientoDebeEnviarCodigo() {
        User usuario = usuarioEjemplo();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        when(codigoVerificacionService.canResend("admin@sgroas.com",
                VerificationCodeService.Type.RESET_PASSWORD)).thenReturn(true);
        when(codigoVerificacionService.generate("admin@sgroas.com",
                VerificationCodeService.Type.RESET_PASSWORD)).thenReturn("112233");

        authService.requestPasswordReset("admin@sgroas.com");

        verify(emailService).sendResetCode("admin@sgroas.com", "112233");
    }

    @Test
    void refreshDebeRotarToken() {
        User usuario = usuarioEjemplo();
        when(tokenService.getEmailFromRefreshToken("refresh-token-prueba"))
                .thenReturn("admin@sgroas.com");
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        simularGeneracionTokens(usuario);

        AuthResponse response = authService.refresh(
                new RefreshTokenRequest("refresh-token-prueba")
        );

        assertNotNull(response);
        assertEquals("access-token-prueba", response.accessToken());
        verify(tokenService).deleteRefreshToken("refresh-token-prueba");
    }

    @Test
    void refreshConUsuarioInactivoDebeLanzarExcepcion() {
        User inactivo = usuarioEjemplo();
        inactivo.setActive(false);
        when(tokenService.getEmailFromRefreshToken("refresh-token-prueba"))
                .thenReturn("admin@sgroas.com");
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(inactivo));

        assertThrows(BadCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequest("refresh-token-prueba")));
    }

    @Test
    void refreshConEmailInexistenteDebeLanzarExcepcion() {
        when(tokenService.getEmailFromRefreshToken("refresh-token-prueba"))
                .thenReturn("desconocido@sgroas.com");
        when(usuarioRepository.findByEmail("desconocido@sgroas.com"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequest("refresh-token-prueba")));
    }

    @Test
    void logoutDebeInvalidarTokens() {
        authService.logout("access-token-prueba",
                new RefreshTokenRequest("refresh-token-prueba"));

        verify(tokenService).addAccessTokenToBlacklist("access-token-prueba");
        verify(tokenService).deleteRefreshToken("refresh-token-prueba");
    }
}
