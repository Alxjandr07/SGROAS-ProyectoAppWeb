package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.RutaAbd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RutaAbdRepository extends JpaRepository<RutaAbd, Integer> {

    boolean existsByTerminalOrigenIdTerminalAndTerminalDestinoIdTerminal(Integer idOrigen, Integer idDestino);

    @Query("select count(p) from Programacion p where p.ruta.idRuta = :idRuta")
    Integer contarProgramaciones(@Param("idRuta") Integer idRuta);
}
