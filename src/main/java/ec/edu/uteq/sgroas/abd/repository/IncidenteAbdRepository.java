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
