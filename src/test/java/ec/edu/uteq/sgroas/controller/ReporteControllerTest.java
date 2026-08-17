package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.exception.GlobalExceptionHandler;
import ec.edu.uteq.sgroas.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService reporteService;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(new ReporteController(reporteService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void estadisticasGeneralesDevuelveDatos() throws Exception {
        when(reporteService.estadisticasGenerales())
                .thenReturn(List.of(Map.of(
                        "total_conductores", 10L,
                        "total_incidentes", 3L)));

        mockMvc().perform(get("/api/reportes/estadisticas-generales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].total_conductores").value(10))
                .andExpect(jsonPath("$[0].total_incidentes").value(3));
    }

    @Test
    void incidentesPorGravedadFiltraPorTipo() throws Exception {
        when(reporteService.incidentesPorGravedad("ROBO")).thenReturn(
                List.of(Map.of("gravedad", "ALTA", "total_incidentes", 2L)));

        mockMvc().perform(get("/api/reportes/incidentes-por-gravedad").param("tipo", "ROBO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gravedad").value("ALTA"));
    }

    @Test
    void incidentesPorRangoConFechas() throws Exception {
        when(reporteService.incidentesPorRango(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(Map.of("incidente_id", 1L, "tipo", "ACCIDENTE")));

        mockMvc().perform(get("/api/reportes/incidentes-por-rango")
                        .param("desde", "2026-01-01T00:00:00Z")
                        .param("hasta", "2026-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("ACCIDENTE"));
    }

    @Test
    void licenciasPorVencerUsaDefaultDe30Dias() throws Exception {
        when(reporteService.licenciasPorVencer(anyInt()))
                .thenReturn(List.of(Map.of("conductor_id", 1L, "cedula", "1234567890")));

        mockMvc().perform(get("/api/reportes/licencias-por-vencer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cedula").value("1234567890"));
    }

    @Test
    void vehiculosEnMantenimientoDevuelveLista() throws Exception {
        when(reporteService.vehiculosEnMantenimiento())
                .thenReturn(List.of(Map.of("placa", "PCH-1234")));

        mockMvc().perform(get("/api/reportes/vehiculos-en-mantenimiento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("PCH-1234"));
    }

    @Test
    void rendimientoRutasDevuelveReporte() throws Exception {
        when(reporteService.reporteRendimientoRutas())
                .thenReturn(List.of(Map.of("ruta_codigo", "R-01", "total_incidentes", 5L)));

        mockMvc().perform(get("/api/reportes/rendimiento-rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruta_codigo").value("R-01"));
    }

    @Test
    void asignacionesActivasDevuelveAsignaciones() throws Exception {
        when(reporteService.asignacionesActivasPorConductor(anyLong()))
                .thenReturn(List.of(Map.of("vehiculo_placa", "PCH-5678")));

        mockMvc().perform(get("/api/reportes/asignaciones-activas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehiculo_placa").value("PCH-5678"));
    }

    @Test
    void incidentesPorGravedadSinParametroNoFalla() throws Exception {
        when(reporteService.incidentesPorGravedad(nullable(String.class)))
                .thenReturn(List.of(Map.of("gravedad", "BAJA", "total_incidentes", 0L)));

        mockMvc().perform(get("/api/reportes/incidentes-por-gravedad"))
                .andExpect(status().isOk());
    }
}