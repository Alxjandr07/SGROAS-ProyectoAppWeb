package ec.edu.uteq.sgroas.security;

import ec.edu.uteq.sgroas.entity.Role;
import ec.edu.uteq.sgroas.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String JWT_SECRET =
            System.getenv().getOrDefault("JWT_SECRET",
                    "TEST_ONLY_SECRET_KEY_2026_NOT_FOR_PRODUCTION_MIN_32");

    private JwtService jwtService;

    @BeforeEach
    void configurarJwtService() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
        ReflectionTestUtils.setField(jwtService, "jwtIssuer", "https://sgroas.uteq.edu.ec");
        ReflectionTestUtils.setField(jwtService, "jwtAudience", "sgroas-frontend");
    }

    private User usuarioEjemplo() {
        return User.builder()
                .id(1L)
                .name("Administrador SGROAS")
                .email("admin@sgroas.com")
                .passwordHash("hash")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();
    }

    @Test
    void generateTokenAllowsExtractingData() {
        String token = jwtService.generateToken(usuarioEjemplo());

        assertNotNull(token);
        assertNotNull(jwtService.extraerJti(token));
        assertEquals("admin@sgroas.com", jwtService.extraerEmail(token));
        assertNotNull(jwtService.extraerExpiracion(token));
        assertEquals(3600000L, jwtService.getExpirationMs());
        assertTrue(jwtService.tokenValido(token, "admin@sgroas.com"));
    }

    @Test
    void tokenConEmailDistintoDebeSerInvalido() {
        String token = jwtService.generateToken(usuarioEjemplo());

        assertFalse(jwtService.tokenValido(token, "otro@sgroas.com"));
    }

    @Test
    void tokenExpiradoDebeSerRechazado() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L);

        String token = jwtService.generateToken(usuarioEjemplo());

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.tokenValido(token, "admin@sgroas.com"));
    }

    @Test
    void extraerExpiracionDebeSerFutura() {
        String token = jwtService.generateToken(usuarioEjemplo());

        assertTrue(jwtService.extraerExpiracion(token).after(new Date()));
    }
}
