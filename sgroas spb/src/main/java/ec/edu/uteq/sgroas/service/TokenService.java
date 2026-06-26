package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public String generarRefreshToken(String email, Long refreshExpirationMs) {
        String refreshToken = UUID.randomUUID().toString();

        String key = "refresh:" + refreshToken;
        redisTemplate.opsForValue().set(
                key,
                email,
                Duration.ofMillis(refreshExpirationMs)
        );

        return refreshToken;
    }

    public String obtenerEmailDesdeRefreshToken(String refreshToken) {
        String key = "refresh:" + refreshToken;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new IllegalArgumentException("Refresh token no valido o expirado");
        }

        return email;
    }

    public void eliminarRefreshToken(String refreshToken) {
        String key = "refresh:" + refreshToken;
        redisTemplate.delete(key);
    }

    public void agregarAccessTokenABlacklist(String accessToken) {
        String jti = jwtService.extraerJti(accessToken);
        long tiempoRestante = jwtService.extraerExpiracion(accessToken).getTime()
                - System.currentTimeMillis();

        if (tiempoRestante > 0) {
            String key = "blacklist:" + jti;
            redisTemplate.opsForValue().set(
                    key,
                    "logout",
                    Duration.ofMillis(tiempoRestante)
            );
        }
    }

    public boolean accessTokenEnBlacklist(String accessToken) {
        String jti = jwtService.extraerJti(accessToken);
        String key = "blacklist:" + jti;

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
