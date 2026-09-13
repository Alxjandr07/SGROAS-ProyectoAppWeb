package ec.edu.uteq.sgroas.dto;

/**
 * Perfil de sesión que viaja en el cuerpo de las respuestas de
 * autenticación. No contiene ningún token: el JWT va solo en la
 * cookie HttpOnly {@code access_token} (Secure + SameSite=Strict).
 * @param nombre nombre del usuario autenticado.
 * @param email correo del usuario autenticado.
 * @param rol rol del usuario autenticado.
 * @param expiresIn segundos hasta el vencimiento de la sesion.
 */
public record SesionResponse(
        String nombre,
        String email,
        String rol,
        Long expiresIn
) {
}
