package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    Page<Ruta> findByActivoTrue(Pageable pageable);

    boolean existsByCodigo(String codigo);

    @Procedure(procedureName = "sp_reporte_rendimiento_rutas", outputParameterName = "cur")
    List<Object[]> reporteRendimientoRutas();
}
