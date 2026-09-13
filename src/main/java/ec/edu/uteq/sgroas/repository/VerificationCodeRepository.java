package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * Consulta el codigo de verificacion mas reciente de un correo y tipo dados.
     * @param email correo asociado al codigo.
     * @param tipo tipo de codigo de verificacion.
     * @return codigo mas reciente si existe.
     */
    Optional<VerificationCode> findFirstByEmailAndTipoOrderByCreadoEnDesc(String email, String tipo);

    /**
     * Elimina los codigos de verificacion de un correo y tipo dados.
     * @param email correo asociado a los codigos.
     * @param tipo tipo de codigo de verificacion.
     */
    void deleteByEmailAndTipo(String email, String tipo);
}
