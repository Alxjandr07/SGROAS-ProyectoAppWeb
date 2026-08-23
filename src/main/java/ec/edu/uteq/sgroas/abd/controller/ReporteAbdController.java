package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.ReporteAbdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/abd/reportes")
@RequiredArgsConstructor
public class ReporteAbdController {

    private final ReporteAbdService reporteAbdService;

    @GetMapping("/resumen")
    public ResponseEntity<AbdDtos.ResumenResponse> resumen() {
        return ResponseEntity.ok(reporteAbdService.resumen());
    }

    @GetMapping("/incidentes-por-nivel")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> incidentesPorNivel() {
        return ResponseEntity.ok(reporteAbdService.incidentesPorNivel());
    }

    @GetMapping("/incidentes-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> incidentesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.incidentesPorEstado());
    }

    @GetMapping("/unidades-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> unidadesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.unidadesPorEstado());
    }

    @GetMapping("/programaciones-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> programacionesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.programacionesPorEstado());
    }

    @GetMapping("/programaciones-por-mes")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> programacionesPorMes() {
        return ResponseEntity.ok(reporteAbdService.programacionesPorMes());
    }

    @GetMapping("/top-rutas")
    public ResponseEntity<List<AbdDtos.TopRutaResponse>> topRutas() {
        return ResponseEntity.ok(reporteAbdService.topRutas());
    }
}
