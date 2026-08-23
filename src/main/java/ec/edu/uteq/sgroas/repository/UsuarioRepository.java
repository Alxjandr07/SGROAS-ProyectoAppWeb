package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByActivoTrue(Pageable pageable);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.activo = true
              AND (:search IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Usuario> buscarActivos(@Param("search") String search, Pageable pageable);

    boolean existsByEmail(String email);
}
