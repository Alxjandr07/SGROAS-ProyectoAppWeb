package ec.edu.uteq.sgroas.abd.repository;

import ec.edu.uteq.sgroas.abd.entity.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    /**
     * Consulta las alertas de forma paginada ordenadas por identificador descendente.
     * @param pageable configuracion de paginacion y ordenamiento.
     * @return pagina con las alertas mas recientes primero.
     */
    Page<Alerta> findAllByOrderByIdAlertaDesc(Pageable pageable);
}
