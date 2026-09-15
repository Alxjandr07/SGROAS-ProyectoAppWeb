package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

public record RouteResponse(
        Long id,
        String code,
        String name,
        String origin,
        String destination,
        Double distanceKm,
        Integer durationMin,
        String status,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
