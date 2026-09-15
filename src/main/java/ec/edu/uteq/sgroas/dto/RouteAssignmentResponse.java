package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

    /**
     * Route assignment data returned by the API after querying or mutating assignments.
     * @param id assignment unique identifier.
     * @param driverId driver assigned to the route.
     * @param driverName driver display name.
     * @param vehicleId vehicle assigned to the route.
     * @param vehiclePlate vehicle plate number.
     * @param routeId route that will be covered.
     * @param routeName route display name.
     * @param assignmentDate date when the assignment was registered.
     * @param startDate date when the assignment starts.
     * @param endDate optional date when the assignment ends.
     * @param status assignment status code.
     * @param active whether the assignment is currently active.
     * @param createdAt registration timestamp.
     * @param updatedAt last update timestamp.
     */
public record RouteAssignmentResponse(
        Long id,
        Long driverId,
        String driverName,
        Long vehicleId,
        String vehiclePlate,
        Long routeId,
        String routeName,
        LocalDate assignmentDate,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
