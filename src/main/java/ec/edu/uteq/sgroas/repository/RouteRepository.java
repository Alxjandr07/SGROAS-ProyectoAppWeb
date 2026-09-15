package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Page<Route> findByActiveTrue(Pageable pageable);

    boolean existsByCode(String code);

    @Procedure(name = "Route.reporteRendimientoRutas")
    List<Object[]> routePerformanceReport();
}
