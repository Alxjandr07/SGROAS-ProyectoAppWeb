package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerminalRepository extends JpaRepository<Terminal, Integer> {

    /**
     * Consulta los terminales de una ciudad ordenados por identificador ascendente.
     * @param idCiudad identificador de la ciudad.
     * @return lista de terminales de la ciudad.
     */
    List<Terminal> findByCiudadIdCiudadOrderByIdTerminalAsc(Integer idCiudad);
}
