package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RouteAssignmentRequest(

        @NotNull(message = "El conductor es obligatorio")
        Long conductorId,

        @NotNull(message = "El vehiculo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "La ruta es obligatoria")
        Long rutaId,

        @NotNull(message = "La fecha de asignacion es obligatoria")
        LocalDate fechaAsignacion,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        @NotNull(message = "El estado es obligatorio")
        String estado
) {
}
