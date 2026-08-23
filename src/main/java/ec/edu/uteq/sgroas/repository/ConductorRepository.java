package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Conductor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

    Page<Conductor> findByActivoTrue(Pageable pageable);

    @Query("""
            SELECT c FROM Conductor c
            WHERE c.activo = true
              AND (:search IS NULL OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR c.cedula LIKE CONCAT('%', :search, '%')
                       OR LOWER(c.numeroLicencia) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Conductor> buscarActivos(@Param("search") String search, Pageable pageable);

    boolean existsByCedula(String cedula);

    boolean existsByNumeroLicencia(String numeroLicencia);

    @Procedure(name = "Conductor.licenciasPorVencer")
    List<Object[]> licenciasPorVencer(@Param("p_dias_umbral") Integer diasUmbral);
}
