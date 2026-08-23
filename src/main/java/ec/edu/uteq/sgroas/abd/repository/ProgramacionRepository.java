package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Programacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProgramacionRepository extends JpaRepository<Programacion, Integer> {

    Page<Programacion> findByEstadoIgnoreCase(String estado, Pageable pageable);

    @Query(value = """
            SELECT * FROM programacion p
            WHERE (CAST(:estado AS text) IS NULL OR LOWER(p.estado) = CAST(:estado AS text))
              AND (CAST(:idConductor AS integer) IS NULL OR p.id_conductor = :idConductor)
              AND (CAST(:idRuta AS integer) IS NULL OR p.id_ruta = :idRuta)
              AND (CAST(:fechaDesde AS date) IS NULL OR p.fecha >= CAST(:fechaDesde AS date))
              AND (CAST(:fechaHasta AS date) IS NULL OR p.fecha <= CAST(:fechaHasta AS date))
            ORDER BY p.id_programacion
            """, nativeQuery = true)
    Page<Programacion> buscarConFiltros(@Param("estado") String estado,
                                        @Param("idConductor") Integer idConductor,
                                        @Param("idRuta") Integer idRuta,
                                        @Param("fechaDesde") LocalDate fechaDesde,
                                        @Param("fechaHasta") LocalDate fechaHasta,
                                        Pageable pageable);

    @Query(value = "SELECT estado AS clave, COUNT(*) AS total FROM programacion GROUP BY estado ORDER BY total DESC",
           nativeQuery = true)
    List<ConteoProjection> contarPorEstado();

    @Query(value = """
            SELECT TO_CHAR(fecha, 'YYYY-MM') AS clave, COUNT(*) AS total
            FROM programacion
            WHERE fecha >= CURRENT_DATE - INTERVAL '180 days'
            GROUP BY TO_CHAR(fecha, 'YYYY-MM')
            ORDER BY clave
            """,
           nativeQuery = true)
    List<ConteoProjection> contarPorMes();
}
