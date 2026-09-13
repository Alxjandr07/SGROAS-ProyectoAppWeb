package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Consulta un usuario por su correo electronico.
     * @param email correo electronico del usuario.
     * @return usuario encontrado si existe.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Consulta los usuarios activos de forma paginada.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los usuarios activos.
     */
    Page<Usuario> findByActivoTrue(Pageable pageable);

    /**
     * Consulta los usuarios activos que coinciden con un texto de busqueda por nombre o correo.
     * @param search texto de busqueda, puede ser nulo para traer todos.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con los usuarios activos encontrados.
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE u.activo = true
              AND (:search IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Usuario> buscarActivos(@Param("search") String search, Pageable pageable);

    /**
     * Consulta si existe un usuario con el correo dado.
     * @param email correo electronico a verificar.
     * @return verdadero si existe un usuario con ese correo.
     */
    boolean existsByEmail(String email);
}
