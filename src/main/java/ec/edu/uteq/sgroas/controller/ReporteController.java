package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Genera el resumen con las estadísticas generales de la operación del sistema.
     * @return respuesta HTTP con la lista de indicadores generales calculados.
     */
    @GetMapping("/estadisticas-generales")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> estadisticasGenerales() {
        return ResponseEntity.ok(reporteService.estadisticasGenerales());
    }

    /**
     * Genera el conteo de incidentes agrupados por nivel de gravedad.
     * @param tipo filtro opcional para limitar el reporte a una clase de incidente.
     * @return respuesta HTTP con la lista de totales por cada nivel de gravedad.
     */
    @GetMapping("/incidentes-por-gravedad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> incidentesPorGravedad(
            @RequestParam(required = false) String tipo
    ) {
        return ResponseEntity.ok(reporteService.incidentesPorGravedad(tipo));
    }

    /**
     * Genera el listado de incidentes ocurridos dentro de un intervalo de tiempo.
     * @param desde fecha y hora inicial del intervalo a consultar.
     * @param hasta fecha y hora final del intervalo a consultar.
     * @return respuesta HTTP con la lista de incidentes del intervalo solicitado.
     */
    @GetMapping("/incidentes-por-rango")
    @PreAuthorize("hasAnyRole('ADMIN', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> incidentesPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta
    ) {
        return ResponseEntity.ok(reporteService.incidentesPorRango(desde, hasta));
    }

    /**
     * Genera el listado de conductores cuyas licencias están próximas a vencer.
     * @param dias cantidad de días hacia adelante para buscar vencimientos cercanos.
     * @return respuesta HTTP con la lista de licencias próximas a vencer.
     */
    @GetMapping("/licencias-por-vencer")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> licenciasPorVencer(
            @RequestParam(defaultValue = "30") Integer dias
    ) {
        return ResponseEntity.ok(reporteService.licenciasPorVencer(dias));
    }

    /**
     * Genera el listado de vehículos que actualmente están en mantenimiento.
     * @return respuesta HTTP con la lista de vehículos en mantenimiento.
     */
    @GetMapping("/vehiculos-en-mantenimiento")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> vehiculosEnMantenimiento() {
        return ResponseEntity.ok(reporteService.vehiculosEnMantenimiento());
    }

    /**
     * Genera el informe de rendimiento operativo de las rutas registradas.
     * @return respuesta HTTP con la lista de métricas de rendimiento por ruta.
     */
    @GetMapping("/rendimiento-rutas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> rendimientoRutas() {
        return ResponseEntity.ok(reporteService.reporteRendimientoRutas());
    }

    /**
     * Genera el listado de asignaciones vigentes de un conductor específico.
     * @param conductorId identificador único del conductor cuyas asignaciones se consultan.
     * @return respuesta HTTP con la lista de asignaciones activas del conductor.
     */
    @GetMapping("/asignaciones-activas/{conductorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> asignacionesActivas(
            @PathVariable Long conductorId
    ) {
        return ResponseEntity.ok(reporteService.asignacionesActivasPorConductor(conductorId));
    }
}