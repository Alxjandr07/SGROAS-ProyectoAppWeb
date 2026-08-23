package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.CodigoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {

    Optional<CodigoVerificacion> findFirstByEmailAndTipoOrderByCreadoEnDesc(String email, String tipo);

    void deleteByEmailAndTipo(String email, String tipo);
}
