package ec.edu.uteq.sgroas.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Respuesta estandar para errores de la API.
 * @param timestamp fecha y hora en que ocurrio el error
 * @param status codigo de estado HTTP del error
 * @param error categoria tecnica del error
 * @param message mensaje legible para el usuario
 * @param path ruta del recurso que genero el error
 * @param details detalles adicionales de validacion por campo
 */
public record ErrorResponse(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {
}
