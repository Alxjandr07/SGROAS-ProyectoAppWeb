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

    /**
     * Atiende la peticion del resumen consolidado con los totales del sistema.
     * @return respuesta con los conteos generales de programaciones, incidentes, alertas, unidades y rutas y estado 200.
     */
    @GetMapping("/resumen")
    public ResponseEntity<AbdDtos.ResumenResponse> resumen() {
        return ResponseEntity.ok(reporteAbdService.resumen());
    }

    /**
     * Atiende la peticion del conteo de incidentes agrupados por nivel de riesgo.
     * @return respuesta con la lista de niveles y sus totales y estado 200.
     */
    @GetMapping("/incidentes-por-nivel")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> incidentesPorNivel() {
        return ResponseEntity.ok(reporteAbdService.incidentesPorNivel());
    }

    /**
     * Atiende la peticion del conteo de incidentes agrupados por estado de atencion.
     * @return respuesta con la lista de estados y sus totales y estado 200.
     */
    @GetMapping("/incidentes-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> incidentesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.incidentesPorEstado());
    }

    /**
     * Atiende la peticion del conteo de unidades agrupadas por estado operativo.
     * @return respuesta con la lista de estados y sus totales y estado 200.
     */
    @GetMapping("/unidades-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> unidadesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.unidadesPorEstado());
    }

    /**
     * Atiende la peticion del conteo de programaciones agrupadas por estado.
     * @return respuesta con la lista de estados y sus totales y estado 200.
     */
    @GetMapping("/programaciones-por-estado")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> programacionesPorEstado() {
        return ResponseEntity.ok(reporteAbdService.programacionesPorEstado());
    }

    /**
     * Atiende la peticion del conteo de programaciones agrupadas por mes previsto del viaje.
     * @return respuesta con la lista de meses y sus totales y estado 200.
     */
    @GetMapping("/programaciones-por-mes")
    public ResponseEntity<List<AbdDtos.ConteoResponse>> programacionesPorMes() {
        return ResponseEntity.ok(reporteAbdService.programacionesPorMes());
    }

    /**
     * Atiende la peticion de las rutas con mayor numero de viajes programados.
     * @return respuesta con la lista de rutas mas utilizadas y sus totales y estado 200.
     */
    @GetMapping("/top-rutas")
    public ResponseEntity<List<AbdDtos.TopRutaResponse>> topRutas() {
        return ResponseEntity.ok(reporteAbdService.topRutas());
    }
}
