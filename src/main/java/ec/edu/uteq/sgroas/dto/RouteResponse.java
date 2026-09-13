package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

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
