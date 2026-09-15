package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

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
