package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.*;

/**
 * Peticion para crear o actualizar una ruta.
 * @param codigo codigo unico de la ruta
 * @param nombre nombre de la ruta
 * @param origen lugar de partida de la ruta
 * @param destino lugar de llegada de la ruta
 * @param distanciaKm distancia de la ruta en kilometros
 * @param duracionEstimadaMin duracion estimada del viaje en minutos
 * @param estado estado de la ruta
 */
public record RouteRequest(

        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 20, message = "El codigo no puede superar los 20 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El origen es obligatorio")
        @Size(max = 150, message = "El origen no puede superar los 150 caracteres")
        String origen,

        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 150, message = "El destino no puede superar los 150 caracteres")
        String destino,

        @NotNull(message = "La distancia es obligatoria")
        @Min(value = 0, message = "La distancia debe ser positiva")
        Double distanciaKm,

        @NotNull(message = "La duracion estimada es obligatoria")
        @Min(value = 1, message = "La duracion minima es 1 minuto")
        Integer duracionEstimadaMin,

        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}
