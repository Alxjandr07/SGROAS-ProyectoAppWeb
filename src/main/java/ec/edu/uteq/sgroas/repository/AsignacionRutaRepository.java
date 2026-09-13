package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.AsignacionRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AsignacionRutaRepository extends JpaRepository<AsignacionRuta, Long> {

    /**
     * Consulta las asignaciones activas de forma paginada con conductor, vehiculo y ruta.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con las asignaciones activas.
     */
    @EntityGraph(attributePaths = {"conductor", "vehiculo", "ruta"})
    Page<AsignacionRuta> findByActivoTrue(Pageable pageable);

    /**
     * Consulta una asignacion por su identificador con el detalle de conductor, vehiculo y ruta.
     * @param id identificador de la asignacion a buscar.
     * @return asignacion con detalle si existe.
     */
    @Query("SELECT a FROM AsignacionRuta a JOIN FETCH a.conductor JOIN FETCH a.vehiculo JOIN FETCH a.ruta WHERE a.id = :id")
    Optional<AsignacionRuta> findWithDetalle(@Param("id") Long id);

    /**
     * Consulta las asignaciones activas de un conductor.
     * @param conductorId identificador del conductor.
     * @return lista de asignaciones activas del conductor.
     */
    List<AsignacionRuta> findByConductorIdAndActivoTrue(Long conductorId);

    /**
     * Consulta las asignaciones activas de un vehiculo.
     * @param vehiculoId identificador del vehiculo.
     * @return lista de asignaciones activas del vehiculo.
     */
    List<AsignacionRuta> findByVehiculoIdAndActivoTrue(Long vehiculoId);

    /**
     * Consulta las asignaciones activas de una ruta.
     * @param rutaId identificador de la ruta.
     * @return lista de asignaciones activas de la ruta.
     */
    List<AsignacionRuta> findByRutaIdAndActivoTrue(Long rutaId);

    /**
     * Consulta las asignaciones activas de un conductor mediante procedimiento almacenado.
     * @param conductorId identificador del conductor.
     * @return filas del procedimiento con las asignaciones activas.
     */
    @Procedure(name = "AsignacionRuta.asignacionesActivasPorConductor")
    List<Object[]> asignacionesActivasPorConductor(@Param("p_conductor_id") Long conductorId);
}
