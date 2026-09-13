package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    /**
     * Consulta las rutas activas de forma paginada.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con las rutas activas.
     */
    Page<Ruta> findByActivoTrue(Pageable pageable);

    /**
     * Consulta si existe una ruta con el codigo dado.
     * @param codigo codigo de ruta a verificar.
     * @return verdadero si existe una ruta con ese codigo.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Consulta el reporte de rendimiento de rutas mediante procedimiento almacenado.
     * @return filas del procedimiento con el rendimiento de las rutas.
     */
    @Procedure(name = "Ruta.reporteRendimientoRutas")
    List<Object[]> reporteRendimientoRutas();
}
