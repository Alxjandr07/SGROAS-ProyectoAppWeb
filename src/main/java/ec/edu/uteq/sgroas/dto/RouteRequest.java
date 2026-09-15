package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.*;

    /**
     * Route data required to create or update a route record.
     * @param code unique route code.
     * @param name route display name.
     * @param origin departure point.
     * @param destination arrival point.
     * @param distanceKm route distance in kilometers.
     * @param durationMin estimated travel time in minutes.
     * @param status route status code.
     */
public record RouteRequest(

        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 20, message = "El codigo no puede superar los 20 caracteres")
        String code,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @NotBlank(message = "El origen es obligatorio")
        @Size(max = 150, message = "El origen no puede superar los 150 caracteres")
        String origin,

        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 150, message = "El destino no puede superar los 150 caracteres")
        String destination,

        @NotNull(message = "La distancia es obligatoria")
        @Min(value = 0, message = "La distancia debe ser positiva")
        Double distanceKm,

        @NotNull(message = "La duracion estimada es obligatoria")
        @Min(value = 1, message = "La duracion minima es 1 minuto")
        Integer durationMin,

        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
