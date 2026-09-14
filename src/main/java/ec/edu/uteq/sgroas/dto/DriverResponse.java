package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Respuesta con los datos completos de un conductor.
 * @param id identificador unico del conductor
 * @param nombres nombres del conductor
 * @param apellidos apellidos del conductor
 * @param cedula cedula del conductor
 * @param numeroLicencia numero de licencia del conductor
 * @param tipoLicencia tipo de licencia del conductor
 * @param fechaVencimientoLicencia fecha de vencimiento de la licencia
 * @param telefono telefono de contacto del conductor
 * @param email correo electronico del conductor
 * @param estado estado operativo del conductor
 * @param activo indica si el conductor esta activo
 * @param licenciaPorVencer indica si la licencia esta proxima a vencer
 * @param creadoEn fecha de creacion del registro
 * @param actualizadoEn fecha de la ultima actualizacion
 */
public record DriverResponse(
        Long id,
        String nombres,
        String apellidos,
        String cedula,
        String numeroLicencia,
        String tipoLicencia,
        LocalDate fechaVencimientoLicencia,
        String telefono,
        String email,
        String estado,
        Boolean activo,
        Boolean licenciaPorVencer,
        Instant creadoEn,
        Instant actualizadoEn
) {
}