package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

public record RouteAssignmentResponse(
        Long id,
        Long conductorId,
        String conductorNombre,
        Long vehiculoId,
        String vehiculoPlaca,
        Long rutaId,
        String rutaNombre,
        LocalDate fechaAsignacion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
