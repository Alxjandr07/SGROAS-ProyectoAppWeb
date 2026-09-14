package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Peticion para crear o actualizar un incidente.
 * @param asignacionId identificador de la asignacion relacionada
 * @param reportadoPor persona que reporta el incidente
 * @param tipo tipo de incidente ocurrido
 * @param descripcion detalle del incidente
 * @param fechaIncidente fecha y hora en que ocurrio
 * @param ubicacion lugar del incidente
 * @param gravedad nivel de gravedad del incidente
 * @param estado estado del incidente
 */
public record IncidentRequest(

        @NotNull(message = "La asignacion es obligatoria")
        Long asignacionId,

        @NotBlank(message = "El reportante es obligatorio")
        @Size(max = 100, message = "El reportante no puede superar los 100 caracteres")
        String reportadoPor,

        @NotBlank(message = "El tipo es obligatorio")
        String tipo,

        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,

        @NotNull(message = "La fecha del incidente es obligatoria")
        LocalDateTime fechaIncidente,

        @Size(max = 255, message = "La ubicacion no puede superar los 255 caracteres")
        String ubicacion,

        @NotBlank(message = "La gravedad es obligatoria")
        String gravedad,

        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}
