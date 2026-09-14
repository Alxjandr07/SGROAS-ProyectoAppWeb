package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Respuesta con los datos completos de una asignacion de ruta.
 * @param id identificador unico de la asignacion
 * @param conductorId identificador del conductor asignado
 * @param conductorNombre nombre del conductor asignado
 * @param vehiculoId identificador del vehiculo asignado
 * @param vehiculoPlaca placa del vehiculo asignado
 * @param rutaId identificador de la ruta asignada
 * @param rutaNombre nombre de la ruta asignada
 * @param fechaAsignacion fecha en que se registro la asignacion
 * @param fechaInicio fecha en que inicia la vigencia
 * @param fechaFin fecha en que finaliza la vigencia
 * @param estado estado de la asignacion
 * @param activo indica si la asignacion esta activa
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
public record RouteAssignmentResponse(
        Long id,
        Long conductorId,
        String conductorNombre,
        Long vehiculoId,
        String vehiculoPlaca,
        Long rutaId,
        String rutaNombre,
        LocalDate fechaAsignacion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
