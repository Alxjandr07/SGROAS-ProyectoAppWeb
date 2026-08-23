package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.IncidenteAbd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidenteAbdRepository extends JpaRepository<IncidenteAbd, Integer> {

    Page<IncidenteAbd> findByEstadoIgnoreCase(String estado, Pageable pageable);

    Page<IncidenteAbd> findByNivelSugeridoIgnoreCase(String nivelSugerido, Pageable pageable);

    @Query(value = """
            SELECT * FROM incidente i
            WHERE (CAST(:estado AS text) IS NULL OR LOWER(i.estado) = CAST(:estado AS text))
              AND (CAST(:nivel AS text) IS NULL OR LOWER(i.nivel_sugerido) = CAST(:nivel AS text))
              AND (CAST(:search AS text) IS NULL OR LOWER(i.tipo) LIKE '%' || CAST(:search AS text) || '%'
                       OR LOWER(i.descripcion) LIKE '%' || CAST(:search AS text) || '%'
                       OR LOWER((SELECT placa FROM unidad WHERE id_unidad = i.id_unidad)) LIKE '%' || CAST(:search AS text) || '%')
            ORDER BY i.id_incidente
            """, nativeQuery = true)
    Page<IncidenteAbd> buscarConFiltros(@Param("estado") String estado,
                                        @Param("nivel") String nivel,
                                        @Param("search") String search,
                                        Pageable pageable);

    @Query("""
            select i.nivelSugerido as nivel, count(i) as total
            from IncidenteAbd i
            group by i.nivelSugerido
            order by total desc
            """)
    List<ConteoPorNivel> contarPorNivel();

    @Query("""
            select i.estado as estado, count(i) as total
            from IncidenteAbd i
            group by i.estado
            """)
    List<ConteoPorEstado> contarPorEstado();

    interface ConteoPorNivel {
        String getNivel();

        Integer getTotal();
    }

    interface ConteoPorEstado {
        String getEstado();

        Integer getTotal();
    }
}
