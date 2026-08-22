package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    List<Terminal> findByCiudadIdCiudadOrderByIdTerminalAsc(Long idCiudad);
}
