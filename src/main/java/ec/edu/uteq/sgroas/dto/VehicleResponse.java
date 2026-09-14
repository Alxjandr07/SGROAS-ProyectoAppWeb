package ec.edu.uteq.sgroas.dto;

import java.time.Instant;

/**
 * Respuesta con los datos completos de un vehiculo.
 * @param id identificador unico del vehiculo
 * @param placa placa del vehiculo
 * @param marca marca del vehiculo
 * @param modelo modelo del vehiculo
 * @param anio anio de fabricacion del vehiculo
 * @param capacidadPasajeros cantidad maxima de pasajeros
 * @param numeroMotor numero de motor del vehiculo
 * @param numeroChasis numero de chasis del vehiculo
 * @param color color del vehiculo
 * @param estado estado del vehiculo
 * @param activo indica si el vehiculo esta activo
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
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
