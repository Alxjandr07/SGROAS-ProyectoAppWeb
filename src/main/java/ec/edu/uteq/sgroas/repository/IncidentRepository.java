package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByActiveTrue(Pageable pageable);

    List<Incident> findByAssignmentIdAndActiveTrue(Long assignmentId);

    @Procedure(name = "Incident.incidentesPorGravedad")
    List<Object[]> incidentsBySeverity(@Param("p_tipo") String tipo);

    @Procedure(name = "Incident.obtenerIncidentesPorRango")
    List<Object[]> getIncidentsByRange(@Param("p_fecha_desde") Instant fechaDesde,
                                             @Param("p_fecha_hasta") Instant fechaHasta);

    @Procedure(name = "Incident.estadisticasGenerales")
    List<Object[]> generalStatistics();
}
