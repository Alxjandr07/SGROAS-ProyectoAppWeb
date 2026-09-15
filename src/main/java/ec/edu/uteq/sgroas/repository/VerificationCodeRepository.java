package ec.edu.uteq.sgroas.repository;

import ec.edu.uteq.sgroas.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findFirstByEmailAndTypeOrderByCreatedAtDesc(String email, String type);

    void deleteByEmailAndType(String email, String type);
}
