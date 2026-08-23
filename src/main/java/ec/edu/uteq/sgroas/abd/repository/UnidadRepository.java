package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Unidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnidadRepository extends JpaRepository<Unidad, Integer> {

    Page<Unidad> findByEstadoIgnoreCase(String estado, Pageable pageable);

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByNumeroDiscoIgnoreCase(String numeroDisco);

    @Query(value = """
            SELECT * FROM unidad u
            WHERE (CAST(:estado AS text) IS NULL OR LOWER(u.estado) = CAST(:estado AS text))
              AND (CAST(:search AS text) IS NULL OR LOWER(u.placa) LIKE '%' || CAST(:search AS text) || '%'
                       OR LOWER(u.numero_disco) LIKE '%' || CAST(:search AS text) || '%'
                       OR LOWER(u.modelo) LIKE '%' || CAST(:search AS text) || '%')
            ORDER BY u.id_unidad
            """, nativeQuery = true)
    Page<Unidad> buscarConFiltros(@Param("estado") String estado,
                                  @Param("search") String search,
                                  Pageable pageable);

    @Query(value = "SELECT estado AS clave, COUNT(*) AS total FROM unidad GROUP BY estado ORDER BY total DESC",
           nativeQuery = true)
    List<ConteoProjection> contarPorEstado();
}
