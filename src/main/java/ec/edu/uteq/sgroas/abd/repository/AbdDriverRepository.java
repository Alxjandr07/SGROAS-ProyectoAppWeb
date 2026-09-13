package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.AbdDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AbdDriverRepository extends JpaRepository<AbdDriver, Integer> {

    /**
     * Consulta los conductores que coinciden con un texto de busqueda por nombres o cedula.
     * @param search texto de busqueda, puede ser nulo para traer todos.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los conductores encontrados.
     */
    @Query("""
            SELECT c FROM AbdDriver c
            WHERE (:search IS NULL OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(c.cedula) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<AbdDriver> buscar(@Param("search") String search, Pageable pageable);
}
