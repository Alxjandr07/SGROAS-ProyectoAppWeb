package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.SgroasApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Prueba de integracion que ejecuta los stored procedures reales
 * (estrategia hibrida CRUD-ORM + SP, ADR-006) contra PostgreSQL.
 * Requiere BD levantada (docker compose up -d postgres redis).
 * Transaccional: los cursores REFCURSOR solo sobreviven dentro de la
 * misma transaccion JDBC (requisito de pgjdbc/PostgreSQL).
 */
@SpringBootTest(classes = SgroasApplication.class)
@ActiveProfiles("test")
@Transactional
class StoredProcedureIntegrationTest {

    @Autowired
    private IncidentRepository incidenteRepository;

    @Autowired
    private DriverRepository conductorRepository;

    @Autowired
    private VehicleRepository vehiculoRepository;

    @Autowired
    private RouteRepository rutaRepository;

    @Autowired
    private RouteAssignmentRepository asignacionRutaRepository;

    @Test
    @DisplayName("fn_estadisticas_generales via @Procedure")
    void estadisticasGenerales() {
        List<Object[]> filas = incidenteRepository.estadisticasGenerales();
        assertNotNull(filas);
    }

    @Test
    @DisplayName("sp_incidentes_por_gravedad via @Procedure")
    void incidentsBySeverity() {
        List<Object[]> filas = incidenteRepository.incidentsBySeverity(null);
        assertNotNull(filas);
    }

    @Test
    @DisplayName("sp_obtener_incidentes_por_rango via @Procedure")
    void incidentsByRange() {
        Instant desde = Instant.now().minus(365, ChronoUnit.DAYS);
        Instant hasta = Instant.now().plus(1, ChronoUnit.DAYS);
        List<Object[]> filas = incidenteRepository.getIncidentsByRange(desde, hasta);
        assertNotNull(filas);
    }

    @Test
    @DisplayName("fn_licencias_por_vencer via @Procedure")
    void licensesExpiring() {
        List<Object[]> filas = conductorRepository.licensesExpiring(30);
        assertNotNull(filas);
    }

    @Test
    @DisplayName("sp_vehiculos_en_mantenimiento via @Procedure")
    void vehiclesInMaintenance() {
        List<Object[]> filas = vehiculoRepository.vehiclesInMaintenance();
        assertNotNull(filas);
    }

    @Test
    @DisplayName("sp_reporte_rendimiento_rutas via @Procedure")
    void routePerformance() {
        List<Object[]> filas = rutaRepository.routePerformanceReport();
        assertNotNull(filas);
    }

    @Test
    @DisplayName("sp_asignaciones_activas_por_conductor via @Procedure")
    void activeAssignments() {
        List<Object[]> filas = asignacionRutaRepository.activeAssignmentsByDriver(1L);
        assertNotNull(filas);
    }
}