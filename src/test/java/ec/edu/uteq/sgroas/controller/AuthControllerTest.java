package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.AuthResponse;
import ec.edu.uteq.sgroas.exception.GlobalExceptionHandler;
import ec.edu.uteq.sgroas.repository.UsuarioRepository;
import ec.edu.uteq.sgroas.security.JwtService;
import ec.edu.uteq.sgroas.security.LoginRateLimiter;
import ec.edu.uteq.sgroas.service.AuthService;
import ec.edu.uteq.sgroas.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private LoginRateLimiter loginRateLimiter;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(
                        new AuthController(authService, loginRateLimiter, jwtService,
                                tokenService, usuarioRepository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private AuthResponse authResponse() {
        return new AuthResponse(
                "access-token", "refresh-token", "Bearer",
                3600000L, "Administrador SGROAS",
                "admin@sgroas.com", "ROLE_ADMIN"
        );
    }

    @Test
    void verificarEmailDebeRetornar200YCookie() throws Exception {
        when(authService.verificarEmail(any(), any())).thenReturn(authResponse());

        mockMvc().perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com",
                                  "codigo": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void olvidarContrasenaDebeRetornar200ConMensajeGenerico() throws Exception {
        mockMvc().perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void restablecerContrasenaDebeRetornar200() throws Exception {
        mockMvc().perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com",
                                  "codigo": "654321",
                                  "nuevaPassword": "nueva-clave-1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void loginCorrectoDebeRetornar200YCookie() throws Exception {
        when(loginRateLimiter.estaBloqueado("127.0.0.1")).thenReturn(false);
        when(authService.login(any())).thenReturn(authResponse());

        mockMvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginConIpBloqueadaDebeRetornar429() throws Exception {
        when(loginRateLimiter.estaBloqueado("127.0.0.1")).thenReturn(true);

        mockMvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.title").value("Demasiadas solicitudes"));
    }

    @Test
    void loginConCredencialesInvalidasDebeRetornar401() throws Exception {
        when(loginRateLimiter.estaBloqueado("127.0.0.1")).thenReturn(false);
        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@sgroas.com",
                                  "password": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshDebeRetornar200YCookie() throws Exception {
        when(authService.refresh(any())).thenReturn(authResponse());

        mockMvc().perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void logoutConCookieDebeRetornar204() throws Exception {
        mockMvc().perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("access_token", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void logoutSinCookieDebeRetornar204() throws Exception {
        mockMvc().perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isNoContent());
    }
}
