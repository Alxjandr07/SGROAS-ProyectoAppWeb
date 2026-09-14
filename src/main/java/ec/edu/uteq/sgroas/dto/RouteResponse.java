package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

/**
 * Respuesta con los datos completos de una ruta.
 * @param id identificador unico de la ruta
 * @param codigo codigo de la ruta
 * @param nombre nombre de la ruta
 * @param origen lugar de partida de la ruta
 * @param destino lugar de llegada de la ruta
 * @param distanciaKm distancia de la ruta en kilometros
 * @param duracionEstimadaMin duracion estimada del viaje en minutos
 * @param estado estado de la ruta
 * @param activo indica si la ruta esta activa
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
public record RouteResponse(
        Long id,
        String codigo,
        String nombre,
        String origen,
        String destino,
        Double distanciaKm,
        Integer duracionEstimadaMin,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
