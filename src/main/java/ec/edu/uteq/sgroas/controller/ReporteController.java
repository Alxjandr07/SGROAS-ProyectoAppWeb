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

    @GetMapping("/estadisticas-generales")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> estadisticasGenerales() {
        return ResponseEntity.ok(reporteService.estadisticasGenerales());
    }

    @GetMapping("/incidentes-por-gravedad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> incidentesPorGravedad(
            @RequestParam(required = false) String tipo
    ) {
        return ResponseEntity.ok(reporteService.incidentesPorGravedad(tipo));
    }

    @GetMapping("/incidentes-por-rango")
    @PreAuthorize("hasAnyRole('ADMIN', 'SEGURIDAD')")
    public ResponseEntity<List<Map<String, Object>>> incidentesPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta
    ) {
        return ResponseEntity.ok(reporteService.incidentesPorRango(desde, hasta));
    }

    @GetMapping("/licencias-por-vencer")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> licenciasPorVencer(
            @RequestParam(defaultValue = "30") Integer dias
    ) {
        return ResponseEntity.ok(reporteService.licenciasPorVencer(dias));
    }

    @GetMapping("/vehiculos-en-mantenimiento")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> vehiculosEnMantenimiento() {
        return ResponseEntity.ok(reporteService.vehiculosEnMantenimiento());
    }

    @GetMapping("/rendimiento-rutas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> rendimientoRutas() {
        return ResponseEntity.ok(reporteService.reporteRendimientoRutas());
    }

    @GetMapping("/asignaciones-activas/{conductorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    public ResponseEntity<List<Map<String, Object>>> asignacionesActivas(
            @PathVariable Long conductorId
    ) {
        return ResponseEntity.ok(reporteService.asignacionesActivasPorConductor(conductorId));
    }
}