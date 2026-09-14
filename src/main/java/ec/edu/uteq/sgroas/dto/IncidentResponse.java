package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Respuesta con los datos completos de un incidente.
 * @param id identificador unico del incidente
 * @param asignacionId identificador de la asignacion relacionada
 * @param reportadoPor persona que reporto el incidente
 * @param tipo tipo de incidente
 * @param descripcion detalle del incidente
 * @param fechaIncidente fecha y hora en que ocurrio
 * @param ubicacion lugar del incidente
 * @param gravedad nivel de gravedad del incidente
 * @param estado estado del incidente
 * @param activo indica si el incidente esta activo
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
public record IncidentResponse(
        Long id,
        Long asignacionId,
        String reportadoPor,
        String tipo,
        String descripcion,
        LocalDateTime fechaIncidente,
        String ubicacion,
        String gravedad,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
