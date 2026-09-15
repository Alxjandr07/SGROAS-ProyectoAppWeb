package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findByActiveTrue(Pageable pageable);

    @Query("""
            SELECT c FROM Driver c
            WHERE c.active = true
              AND (:search IS NULL OR LOWER(c.firstNames) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(c.lastNames) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR c.nationalId LIKE CONCAT('%', :search, '%')
                       OR LOWER(c.licenseNumber) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Driver> searchActive(@Param("search") String search, Pageable pageable);

    boolean existsByNationalId(String nationalId);

    boolean existsByLicenseNumber(String licenseNumber);

    @Procedure(name = "Driver.licenciasPorVencer")
    List<Object[]> licensesExpiring(@Param("p_dias_umbral") Integer diasUmbral);
}
