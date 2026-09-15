package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

    /**
     * Route data returned by the API after querying or mutating routes.
     * @param id route unique identifier.
     * @param code unique route code.
     * @param name route display name.
     * @param origin departure point.
     * @param destination arrival point.
     * @param distanceKm route distance in kilometers.
     * @param durationMin estimated travel time in minutes.
     * @param status route status code.
     * @param active whether the route is currently active.
     * @param createdAt registration timestamp.
     * @param updatedAt last update timestamp.
     */
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
