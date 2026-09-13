package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Incidente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    /**
     * Consulta los incidentes activos de forma paginada.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los incidentes activos.
     */
    Page<Incidente> findByActivoTrue(Pageable pageable);

    /**
     * Consulta los incidentes activos de una asignacion.
     * @param asignacionId identificador de la asignacion.
     * @return lista de incidentes activos de la asignacion.
     */
    List<Incidente> findByAsignacionIdAndActivoTrue(Long asignacionId);

    /**
     * Consulta los incidentes agrupados por gravedad mediante procedimiento almacenado.
     * @param tipo tipo de incidente para filtrar.
     * @return filas del procedimiento con el conteo por gravedad.
     */
    @Procedure(name = "Incidente.incidentesPorGravedad")
    List<Object[]> incidentesPorGravedad(@Param("p_tipo") String tipo);

    /**
     * Consulta los incidentes dentro de un rango de fechas mediante procedimiento almacenado.
     * @param fechaDesde fecha inicial del rango.
     * @param fechaHasta fecha final del rango.
     * @return filas del procedimiento con los incidentes del rango.
     */
    @Procedure(name = "Incidente.obtenerIncidentesPorRango")
    List<Object[]> obtenerIncidentesPorRango(@Param("p_fecha_desde") Instant fechaDesde,
                                             @Param("p_fecha_hasta") Instant fechaHasta);

    /**
     * Consulta las estadisticas generales de incidentes mediante procedimiento almacenado.
     * @return filas del procedimiento con las estadisticas generales.
     */
    @Procedure(name = "Incidente.estadisticasGenerales")
    List<Object[]> estadisticasGenerales();
}
