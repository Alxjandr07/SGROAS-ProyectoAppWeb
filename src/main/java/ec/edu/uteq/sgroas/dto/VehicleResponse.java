package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

public record VehicleResponse(
        Long id,
        String plate,
        String brand,
        String model,
        Integer year,
        Integer capacity,
        String engineNumber,
        String chassisNumber,
        String color,
        String status,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
