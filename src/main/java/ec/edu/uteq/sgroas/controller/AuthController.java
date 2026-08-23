package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.AuthResponse;
import ec.edu.uteq.sgroas.dto.LoginRequest;
import ec.edu.uteq.sgroas.dto.RefreshTokenRequest;
import ec.edu.uteq.sgroas.dto.RegisterRequest;
import ec.edu.uteq.sgroas.entity.Usuario;
import ec.edu.uteq.sgroas.repository.UsuarioRepository;
import ec.edu.uteq.sgroas.security.JwtService;
import ec.edu.uteq.sgroas.security.LoginRateLimiter;
import ec.edu.uteq.sgroas.service.AuthService;
import ec.edu.uteq.sgroas.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(
            @CookieValue(name = "access_token", required = false) String accessTokenCookie
    ) {
        if (accessTokenCookie == null || accessTokenCookie.isBlank()
                || tokenService.accessTokenEnBlacklist(accessTokenCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = jwtService.extraerEmail(accessTokenCookie);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::getActivo)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new AuthResponse(
                "",
                "",
                "Bearer",
                jwtService.getExpirationMs(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        if (loginRateLimiter.estaBloqueado(ip)) {
            ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
            detail.setTitle("Demasiadas solicitudes");
            detail.setDetail("Has superado el limite de intentos de inicio de sesion. Espera 60 segundos.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(detail);
        }
        try {
            AuthResponse response = authService.login(request);
            loginRateLimiter.resetear(ip);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, crearCookieAccessToken(response.accessToken()))
                    .body(response);
        } catch (Exception e) {
            loginRateLimiter.registrarIntentoFallido(ip);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, crearCookieAccessToken(response.accessToken()))
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "access_token", required = false) String accessTokenCookie,
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        String accessToken = accessTokenCookie;
        if (accessToken == null) {
            accessToken = "";
        }
        authService.logout(accessToken, request);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, eliminarCookieAccessToken())
                .build();
    }

    private String crearCookieAccessToken(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build()
                .toString();
    }

    private String eliminarCookieAccessToken() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }
}