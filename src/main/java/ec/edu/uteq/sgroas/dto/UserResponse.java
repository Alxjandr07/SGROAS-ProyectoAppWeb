package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

/**
 * Respuesta con los datos completos de un usuario.
 * @param id identificador unico del usuario
 * @param nombre nombre del usuario
 * @param email correo electronico del usuario
 * @param rol rol asignado al usuario
 * @param activo indica si el usuario esta activo
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
public record UserResponse(
        Long id,
        String nombre,
        String email,
        String rol,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
