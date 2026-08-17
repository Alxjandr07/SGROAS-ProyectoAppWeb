package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.repository.AsignacionRutaRepository;
import ec.edu.uteq.sgroas.repository.ConductorRepository;
import ec.edu.uteq.sgroas.repository.IncidenteRepository;
import ec.edu.uteq.sgroas.repository.RutaRepository;
import ec.edu.uteq.sgroas.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de reportes que invoca los stored procedures a traves de
 * @Procedure (estrategia hibrida CRUD-ORM + SP, ver ADR-006).
 * Transaccional: los cursores REFCURSOR de PostgreSQL solo pueden leerse
 * dentro de la misma transaccion JDBC (ver CATALOGO-SP.md).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final IncidenteRepository incidenteRepository;
    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;
    private final AsignacionRutaRepository asignacionRutaRepository;

    public List<Map<String, Object>> estadisticasGenerales() {
        return incidenteRepository.estadisticasGenerales().stream()
                .map(fila -> mapa(
                        "total_conductores", fila[0],
                        "conductores_activos", fila[1],
                        "total_vehiculos", fila[2],
                        "vehiculos_activos", fila[3],
                        "total_rutas", fila[4],
                        "rutas_activas", fila[5],
                        "total_asignaciones", fila[6],
                        "asignaciones_activas", fila[7],
                        "total_incidentes", fila[8],
                        "incidentes_abiertos", fila[9]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> incidentesPorGravedad(String tipo) {
        return incidenteRepository.incidentesPorGravedad(tipo).stream()
                .map(fila -> mapa(
                        "gravedad", fila[0],
                        "total_incidentes", fila[1],
                        "ultimo_incidente", fila[2]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> incidentesPorRango(Instant desde, Instant hasta) {
        return incidenteRepository.obtenerIncidentesPorRango(desde, hasta).stream()
                .map(fila -> mapa(
                        "incidente_id", fila[0],
                        "tipo", fila[1],
                        "gravedad", fila[2],
                        "estado", fila[3],
                        "descripcion", fila[4],
                        "fecha_incidente", fila[5],
                        "ubicacion", fila[6],
                        "conductor_nombre", fila[7],
                        "vehiculo_placa", fila[8],
                        "ruta_codigo", fila[9]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> licenciasPorVencer(Integer dias) {
        return conductorRepository.licenciasPorVencer(dias).stream()
                .map(fila -> mapa(
                        "conductor_id", fila[0],
                        "nombre_completo", fila[1],
                        "cedula", fila[2],
                        "numero_licencia", fila[3],
                        "tipo_licencia", fila[4],
                        "fecha_vencimiento", fila[5],
                        "asignacion_activa", fila[6]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> vehiculosEnMantenimiento() {
        return vehiculoRepository.vehiculosEnMantenimiento().stream()
                .map(fila -> mapa(
                        "vehiculo_id", fila[0],
                        "placa", fila[1],
                        "marca", fila[2],
                        "modelo", fila[3],
                        "anio", fila[4],
                        "total_asignaciones", fila[5],
                        "total_incidentes", fila[6]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> reporteRendimientoRutas() {
        return rutaRepository.reporteRendimientoRutas().stream()
                .map(fila -> mapa(
                        "ruta_id", fila[0],
                        "ruta_codigo", fila[1],
                        "ruta_nombre", fila[2],
                        "total_asignaciones", fila[3],
                        "total_incidentes", fila[4],
                        "incidentes_criticos", fila[5],
                        "incidentes_altos", fila[6],
                        "incidentes_medios", fila[7],
                        "incidentes_bajos", fila[8],
                        "promedio_distancia_km", fila[9]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> asignacionesActivasPorConductor(Long conductorId) {
        return asignacionRutaRepository.asignacionesActivasPorConductor(conductorId).stream()
                .map(fila -> mapa(
                        "asignacion_id", fila[0],
                        "vehiculo_placa", fila[1],
                        "vehiculo_marca", fila[2],
                        "ruta_codigo", fila[3],
                        "ruta_nombre", fila[4],
                        "fecha_inicio", fila[5],
                        "fecha_fin", fila[6]))
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapa(Object... pares) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            m.put((String) pares[i], pares[i + 1]);
        }
        return m;
    }
}