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

    /**
     * Consulta los conductores activos de forma paginada.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los conductores activos.
     */
    Page<Conductor> findByActivoTrue(Pageable pageable);

    /**
     * Consulta los conductores activos que coinciden con un texto de busqueda por nombres, apellidos, cedula o licencia.
     * @param search texto de busqueda, puede ser nulo para traer todos.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los conductores activos encontrados.
     */
    @Query("""
            SELECT c FROM Conductor c
            WHERE c.activo = true
              AND (:search IS NULL OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR c.cedula LIKE CONCAT('%', :search, '%')
                       OR LOWER(c.numeroLicencia) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Conductor> buscarActivos(@Param("search") String search, Pageable pageable);

    /**
     * Consulta si existe un conductor con la cedula dada.
     * @param cedula cedula a verificar.
     * @return verdadero si existe un conductor con esa cedula.
     */
    boolean existsByCedula(String cedula);

    /**
     * Consulta si existe un conductor con el numero de licencia dado.
     * @param numeroLicencia numero de licencia a verificar.
     * @return verdadero si existe un conductor con esa licencia.
     */
    boolean existsByNumeroLicencia(String numeroLicencia);

    /**
     * Consulta los conductores con licencia proxima a vencer mediante procedimiento almacenado.
     * @param diasUmbral cantidad de dias de anticipacion para el vencimiento.
     * @return filas del procedimiento con las licencias por vencer.
     */
    @Procedure(name = "Conductor.licenciasPorVencer")
    List<Object[]> licenciasPorVencer(@Param("p_dias_umbral") Integer diasUmbral);
}
