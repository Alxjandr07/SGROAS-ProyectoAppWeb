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

    Page<Incidente> findByActivoTrue(Pageable pageable);

    List<Incidente> findByAsignacionIdAndActivoTrue(Long asignacionId);

    @Procedure(name = "Incidente.incidentesPorGravedad")
    List<Object[]> incidentesPorGravedad(@Param("p_tipo") String tipo);

    @Procedure(name = "Incidente.obtenerIncidentesPorRango")
    List<Object[]> obtenerIncidentesPorRango(@Param("p_fecha_desde") Instant fechaDesde,
                                             @Param("p_fecha_hasta") Instant fechaHasta);

    @Procedure(name = "Incidente.estadisticasGenerales")
    List<Object[]> estadisticasGenerales();
}
