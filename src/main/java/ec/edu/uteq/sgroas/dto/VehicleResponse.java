package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

public record VehicleResponse(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer anio,
        Integer capacidadPasajeros,
        String numeroMotor,
        String numeroChasis,
        String color,
        String estado,
        Boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}
