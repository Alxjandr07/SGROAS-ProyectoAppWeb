package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Programacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgramacionRepository extends JpaRepository<Programacion, Long> {

    Page<Programacion> findByEstadoIgnoreCase(String estado, Pageable pageable);

    @Query("""
            select p from Programacion p
            where (:estado is null or lower(p.estado) = lower(:estado))
              and (:idConductor is null or p.conductor.idConductor = :idConductor)
              and (:idRuta is null or p.ruta.idRuta = :idRuta)
            """)
    Page<Programacion> buscarConFiltros(@Param("estado") String estado,
                                        @Param("idConductor") Long idConductor,
                                        @Param("idRuta") Long idRuta,
                                        Pageable pageable);
}
