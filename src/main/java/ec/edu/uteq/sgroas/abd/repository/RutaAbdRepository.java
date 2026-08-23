package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.RutaAbd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RutaAbdRepository extends JpaRepository<RutaAbd, Integer> {

    boolean existsByTerminalOrigenIdTerminalAndTerminalDestinoIdTerminal(Integer idOrigen, Integer idDestino);

    @Query("""
            SELECT COUNT(p) FROM Programacion p WHERE p.ruta.idRuta = :idRuta
            """)
    long contarProgramaciones(@Param("idRuta") Integer idRuta);

    @Query("""
            SELECT r FROM RutaAbd r
            WHERE (:search IS NULL
                       OR LOWER(r.terminalOrigen.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(r.terminalDestino.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(r.terminalOrigen.ciudad.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(r.terminalDestino.ciudad.nombre) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<RutaAbd> buscar(@Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT r.id_ruta AS id,
                   t1.nombre || ' -> ' || t2.nombre AS descripcion,
                   COUNT(p.id_programacion) AS total
            FROM programacion p
            JOIN ruta r ON r.id_ruta = p.id_ruta
            JOIN terminal t1 ON t1.id_terminal = r.id_terminal_origen
            JOIN terminal t2 ON t2.id_terminal = r.id_terminal_destino
            GROUP BY r.id_ruta, t1.nombre, t2.nombre
            ORDER BY total DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopRutaProjection> topRutas();
}
