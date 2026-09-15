package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

    /**
     * Vehicle data returned by the API after querying or mutating vehicles.
     * @param id vehicle unique identifier.
     * @param plate vehicle plate number.
     * @param brand vehicle brand.
     * @param model vehicle model.
     * @param year manufacturing year.
     * @param capacity passenger capacity.
     * @param engineNumber engine number.
     * @param chassisNumber chassis number.
     * @param color vehicle color.
     * @param status vehicle status code.
     * @param active whether the vehicle is currently active.
     * @param createdAt registration timestamp.
     * @param updatedAt last update timestamp.
     */
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
