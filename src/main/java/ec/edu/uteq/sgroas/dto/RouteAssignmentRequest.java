package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RouteAssignmentRequest(

        @NotNull(message = "El conductor es obligatorio")
        Long driverId,

        @NotNull(message = "El vehiculo es obligatorio")
        Long vehicleId,

        @NotNull(message = "La ruta es obligatoria")
        Long routeId,

        @NotNull(message = "La fecha de asignacion es obligatoria")
        LocalDate assignmentDate,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "El estado es obligatorio")
        String status
) {
}
