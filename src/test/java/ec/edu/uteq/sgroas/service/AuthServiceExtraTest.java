package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.AuthResponse;
import ec.edu.uteq.sgroas.dto.LoginRequest;
import ec.edu.uteq.sgroas.dto.RefreshTokenRequest;
import ec.edu.uteq.sgroas.entity.Rol;
import ec.edu.uteq.sgroas.entity.Usuario;
import ec.edu.uteq.sgroas.exception.CorreoNoVerificadoException;
import ec.edu.uteq.sgroas.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private CodigoVerificacionService codigoVerificacionService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void configurarRefreshExpiration() {
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);
    }

    private Usuario usuarioEjemplo() {
        return Usuario.builder()
                .id(1L)
                .nombre("Administrador SGROAS")
                .email("admin@sgroas.com")
                .passwordHash("password-encriptado")
                .rol(Rol.ROLE_ADMIN)
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();
    }

    private void simularGeneracionTokens(Usuario usuario) {
        when(jwtService.generarToken(usuario)).thenReturn("access-token-prueba");
        when(tokenService.generarRefreshToken(eq("admin@sgroas.com"), eq(604800000L)))
                .thenReturn("refresh-token-prueba");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
    }

    @Test
    void verificarEmailCorrectoDebeActivarCuentaYRetornarTokens() {
        Usuario usuario = usuarioEjemplo();
        usuario.setActivo(false);
        usuario.setVerificado(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        simularGeneracionTokens(usuario);

        AuthResponse response = authService.verificarEmail("admin@sgroas.com", "654321");

        assertEquals("access-token-prueba", response.accessToken());
        verify(codigoVerificacionService).validar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.VERIFICACION, "654321");
        verify(usuarioRepository).save(argThat(u ->
                Boolean.TRUE.equals(u.getActivo()) && Boolean.TRUE.equals(u.getVerificado())));
    }

    @Test
    void loginConCorreoNoVerificadoDebeLanzarExcepcion() {
        Usuario sinVerificar = usuarioEjemplo();
        sinVerificar.setActivo(false);
        sinVerificar.setVerificado(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(sinVerificar));
        when(passwordEncoder.matches("123456", "password-encriptado")).thenReturn(true);

        assertThrows(CorreoNoVerificadoException.class,
                () -> authService.login(new LoginRequest("admin@sgroas.com", "123456")));
    }

    @Test
    void restablecerContrasenaDebeActualizarClave() {
        Usuario usuario = usuarioEjemplo();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nueva-clave-1")).thenReturn("hash-nuevo");

        authService.restablecerContrasena("admin@sgroas.com", "111222", "nueva-clave-1");

        verify(codigoVerificacionService).validar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.RESET_PASSWORD, "111222");
        verify(usuarioRepository).save(argThat(u -> "hash-nuevo".equals(u.getPasswordHash())));
    }

    @Test
    void reenviarCodigoDebeGenerarYEnviarNuevoCodigo() {
        Usuario sinVerificar = usuarioEjemplo();
        sinVerificar.setVerificado(false);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(sinVerificar));
        when(codigoVerificacionService.puedeReenviar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.VERIFICACION)).thenReturn(true);
        when(codigoVerificacionService.generar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.VERIFICACION)).thenReturn("999888");

        authService.reenviarCodigoVerificacion("admin@sgroas.com");

        verify(emailService).enviarCodigoVerificacion(
                "admin@sgroas.com", "Administrador SGROAS", "999888");
    }

    @Test
    void reenviarCodigoConCuentaVerificadaNoDebeEnviarNada() {
        Usuario verificado = usuarioEjemplo();
        verificado.setVerificado(true);
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(verificado));

        authService.reenviarCodigoVerificacion("admin@sgroas.com");

        verifyNoInteractions(emailService);
    }

    @Test
    void solicitarRestablecimientoDebeEnviarCodigo() {
        Usuario usuario = usuarioEjemplo();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        when(codigoVerificacionService.puedeReenviar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.RESET_PASSWORD)).thenReturn(true);
        when(codigoVerificacionService.generar("admin@sgroas.com",
                CodigoVerificacionService.Tipo.RESET_PASSWORD)).thenReturn("112233");

        authService.solicitarRestablecimiento("admin@sgroas.com");

        verify(emailService).enviarCodigoRestablecimiento("admin@sgroas.com", "112233");
    }

    @Test
    void refreshDebeRotarToken() {
        Usuario usuario = usuarioEjemplo();
        when(tokenService.obtenerEmailDesdeRefreshToken("refresh-token-prueba"))
                .thenReturn("admin@sgroas.com");
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));
        simularGeneracionTokens(usuario);

        AuthResponse response = authService.refresh(
                new RefreshTokenRequest("refresh-token-prueba")
        );

        assertNotNull(response);
        assertEquals("access-token-prueba", response.accessToken());
        verify(tokenService).eliminarRefreshToken("refresh-token-prueba");
    }

    @Test
    void refreshConUsuarioInactivoDebeLanzarExcepcion() {
        Usuario inactivo = usuarioEjemplo();
        inactivo.setActivo(false);
        when(tokenService.obtenerEmailDesdeRefreshToken("refresh-token-prueba"))
                .thenReturn("admin@sgroas.com");
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(inactivo));

        assertThrows(BadCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequest("refresh-token-prueba")));
    }

    @Test
    void refreshConEmailInexistenteDebeLanzarExcepcion() {
        when(tokenService.obtenerEmailDesdeRefreshToken("refresh-token-prueba"))
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

        verify(tokenService).agregarAccessTokenABlacklist("access-token-prueba");
        verify(tokenService).eliminarRefreshToken("refresh-token-prueba");
    }
}
