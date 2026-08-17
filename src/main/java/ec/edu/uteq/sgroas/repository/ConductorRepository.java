package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Conductor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

    Page<Conductor> findByActivoTrue(Pageable pageable);

    boolean existsByCedula(String cedula);

    boolean existsByNumeroLicencia(String numeroLicencia);

    @Procedure(name = "Conductor.licenciasPorVencer")
    List<Object[]> licenciasPorVencer(@Param("p_dias_umbral") Integer diasUmbral);
}
