package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

    /**
     * Route assignment data required to create or update an assignment.
     * @param driverId driver assigned to the route.
     * @param vehicleId vehicle assigned to the route.
     * @param routeId route that will be covered.
     * @param assignmentDate date when the assignment was registered.
     * @param startDate date when the assignment starts.
     * @param endDate optional date when the assignment ends.
     * @param status assignment status code.
     */
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
