package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Unidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    Page<Unidad> findByEstadoIgnoreCase(String estado, Pageable pageable);

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByNumeroDiscoIgnoreCase(String numeroDisco);
}
