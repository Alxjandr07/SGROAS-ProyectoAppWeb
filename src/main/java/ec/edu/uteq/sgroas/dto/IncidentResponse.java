package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        Long asignacionId,
        String reportadoPor,
        String tipo,
        String descripcion,
        LocalDateTime fechaIncidente,
        String ubicacion,
        String gravedad,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
