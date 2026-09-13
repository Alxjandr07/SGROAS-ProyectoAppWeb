package ec.edu.uteq.sgroas.security;

import ec.edu.uteq.sgroas.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    @Value("${app.jwt.issuer}")
    private String jwtIssuer;

    @Value("${app.jwt.audience}")
    private String jwtAudience;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Crea un token firmado con los datos del usuario para autenticar sus peticiones.
     * @param usuario entidad con correo, nombre y rol que se guardan en el token
     * @return token compacto listo para enviar en cabecera o cookie
     */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(jti)
                .issuer(jwtIssuer)
                .subject(usuario.getEmail())
                .audience().add(jwtAudience).and()
                .issuedAt(ahora)
                .notBefore(ahora)
                .expiration(expiracion)
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Recupera el correo del propietario a partir de un token firmado.
     * @param token texto compacto previamente generado por este servicio
     * @return correo guardado en el asunto del token
     */
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Recupera el identificador unico del token para controlar revocaciones.
     * @param token texto compacto previamente generado por este servicio
     * @return identificador unico asignado al crear el token
     */
    public String extraerJti(String token) {
        return extraerClaim(token, Claims::getId);
    }

    /**
     * Recupera la fecha de vencimiento contenida en un token firmado.
     * @param token texto compacto previamente generado por este servicio
     * @return fecha a partir de la cual el token deja de aceptarse
     */
    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    /**
     * Informa el tiempo de vida configurado para los tokens emitidos.
     * @return duracion de vigencia en milisegundos definida en propiedades
     */
    public Long getExpirationMs() {
        return jwtExpirationMs;
    }

    /**
     * Comprueba que un token pertenezca al usuario esperado y siga vigente.
     * @param token texto compacto a validar con firma y fecha de expiracion
     * @param email correo esperado del propietario para comparar con el asunto
     * @return verdadero cuando el correo coincide y el token no ha expirado
     */
    public boolean tokenValido(String token, String email) {
        String emailToken = extraerEmail(token);
        return emailToken.equals(email) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
