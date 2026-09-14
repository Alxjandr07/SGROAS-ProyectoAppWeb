package ec.edu.uteq.sgroas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Peticion para crear o actualizar una asignacion de ruta.
 * @param conductorId identificador del conductor asignado
 * @param vehiculoId identificador del vehiculo asignado
 * @param rutaId identificador de la ruta asignada
 * @param fechaAsignacion fecha en que se registro la asignacion
 * @param fechaInicio fecha en que inicia la vigencia
 * @param fechaFin fecha en que finaliza la vigencia
 * @param estado estado de la asignacion
 */
public record RouteAssignmentRequest(

        @NotNull(message = "El conductor es obligatorio")
        Long conductorId,

        @NotNull(message = "El vehiculo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "La ruta es obligatoria")
        Long rutaId,

        @NotNull(message = "La fecha de asignacion es obligatoria")
        LocalDate fechaAsignacion,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        @NotNull(message = "El estado es obligatorio")
        String estado
) {
}
