package ec.edu.uteq.sgroas.dto;

/**
 * Respuesta de autenticacion con los tokens y los datos basicos del usuario.
 * @param accessToken token de acceso para las peticiones autenticadas.
 * @param refreshToken token para renovar la sesion.
 * @param tokenType tipo de token emitido.
 * @param expiresIn segundos hasta el vencimiento del token de acceso.
 * @param nombre nombre del usuario autenticado.
 * @param email correo del usuario autenticado.
 * @param rol rol del usuario autenticado.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String nombre,
        String email,
        String rol
) {
}