package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

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
