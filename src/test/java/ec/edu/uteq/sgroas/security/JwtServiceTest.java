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
            "SGROAS_SECRET_KEY_DE_DESARROLLO_2026_ENTREGA_1B_CON_MINIMO_32_CARACTERES";

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
                .nombre("Administrador SGROAS")
                .email("admin@sgroas.com")
                .passwordHash("hash")
                .rol(Role.ROLE_ADMIN)
                .activo(true)
                .build();
    }

    @Test
    void generarTokenDebePermitirExtraerDatos() {
        String token = jwtService.generarToken(usuarioEjemplo());

        assertNotNull(token);
        assertNotNull(jwtService.extraerJti(token));
        assertEquals("admin@sgroas.com", jwtService.extraerEmail(token));
        assertNotNull(jwtService.extraerExpiracion(token));
        assertEquals(3600000L, jwtService.getExpirationMs());
        assertTrue(jwtService.tokenValido(token, "admin@sgroas.com"));
    }

    @Test
    void tokenConEmailDistintoDebeSerInvalido() {
        String token = jwtService.generarToken(usuarioEjemplo());

        assertFalse(jwtService.tokenValido(token, "otro@sgroas.com"));
    }

    @Test
    void tokenExpiradoDebeSerRechazado() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L);

        String token = jwtService.generarToken(usuarioEjemplo());

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.tokenValido(token, "admin@sgroas.com"));
    }

    @Test
    void extraerExpiracionDebeSerFutura() {
        String token = jwtService.generarToken(usuarioEjemplo());

        assertTrue(jwtService.extraerExpiracion(token).after(new Date()));
    }
}
