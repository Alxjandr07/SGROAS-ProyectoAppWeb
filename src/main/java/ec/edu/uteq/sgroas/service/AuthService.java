package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.AuthResponse;
import ec.edu.uteq.sgroas.dto.LoginRequest;
import ec.edu.uteq.sgroas.dto.RefreshTokenRequest;
import ec.edu.uteq.sgroas.entity.Usuario;
import ec.edu.uteq.sgroas.exception.CorreoNoVerificadoException;
import ec.edu.uteq.sgroas.repository.UsuarioRepository;
import ec.edu.uteq.sgroas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Autenticacion de SGROAS. No existe registro publico: los usuarios los crea
 * el ADMIN desde el modulo Usuarios (UsuarioService), que envia por correo un
 * codigo de activacion; aqui solo se valida ese codigo y se gestionan las
 * sesiones y el restablecimiento de contrasena.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final CodigoVerificacionService codigoVerificacionService;
    private final EmailService emailService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        if (Boolean.FALSE.equals(usuario.getVerificado())) {
            throw new CorreoNoVerificadoException(
                    "Tu cuenta aun no esta verificada. Revisa tu correo e ingresa el codigo de 6 digitos.");
        }

        if (!usuario.getActivo()) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        return generarRespuestaAutenticacion(usuario);
    }

    /** Confirma el codigo de activacion enviado por el administrador e inicia sesion. */
    public AuthResponse verificarEmail(String email, String codigo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con ese email"));

        codigoVerificacionService.validar(email, CodigoVerificacionService.Tipo.VERIFICACION, codigo);

        usuario.setVerificado(true);
        usuario.setActivo(true);
        usuario.setActualizadoEn(Instant.now());
        usuarioRepository.save(usuario);

        return generarRespuestaAutenticacion(usuario);
    }

    /** Reenvia el codigo de activacion si la cuenta sigue sin verificar y respetando la espera minima. */
    public void reenviarCodigoVerificacion(String email) {
        usuarioRepository.findByEmail(email)
                .filter(u -> Boolean.FALSE.equals(u.getVerificado()))
                .filter(u -> codigoVerificacionService.puedeReenviar(email,
                        CodigoVerificacionService.Tipo.VERIFICACION))
                .ifPresent(this::enviarCodigoActivacion);
    }

    /** Envia un codigo para restablecer la contrasena (respuesta siempre generica en el controlador). */
    public void solicitarRestablecimiento(String email) {
        usuarioRepository.findByEmail(email)
                .filter(Usuario::getActivo)
                .filter(u -> codigoVerificacionService.puedeReenviar(email,
                        CodigoVerificacionService.Tipo.RESET_PASSWORD))
                .ifPresent(u -> {
                    String codigo = codigoVerificacionService.generar(email,
                            CodigoVerificacionService.Tipo.RESET_PASSWORD);
                    emailService.enviarCodigoRestablecimiento(email, codigo);
                });
    }

    /** Valida el codigo y cambia la contrasena de la cuenta. */
    public void restablecerContrasena(String email, String codigo, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con ese email"));

        codigoVerificacionService.validar(email, CodigoVerificacionService.Tipo.RESET_PASSWORD, codigo);

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setActivo(true);
        usuario.setVerificado(true);
        usuario.setActualizadoEn(Instant.now());
        usuarioRepository.save(usuario);
    }

    private void enviarCodigoActivacion(Usuario usuario) {
        String codigo = codigoVerificacionService.generar(usuario.getEmail(),
                CodigoVerificacionService.Tipo.VERIFICACION);
        emailService.enviarCodigoVerificacion(usuario.getEmail(), usuario.getNombre(), codigo);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String email = tokenService.obtenerEmailDesdeRefreshToken(request.refreshToken());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new BadCredentialsException("Usuario no valido"));

        tokenService.eliminarRefreshToken(request.refreshToken());

        return generarRespuestaAutenticacion(usuario);
    }

    public void logout(String accessToken, RefreshTokenRequest request) {
        tokenService.agregarAccessTokenABlacklist(accessToken);
        tokenService.eliminarRefreshToken(request.refreshToken());
    }

    private AuthResponse generarRespuestaAutenticacion(Usuario usuario) {
        String accessToken = jwtService.generarToken(usuario);
        String refreshToken = tokenService.generarRefreshToken(
                usuario.getEmail(),
                refreshExpirationMs
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationMs(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
}