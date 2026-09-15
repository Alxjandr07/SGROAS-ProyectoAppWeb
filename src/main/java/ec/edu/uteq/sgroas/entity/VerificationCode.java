package ec.edu.uteq.sgroas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "codigos_verificacion")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** Hash SHA-256 del codigo de 6 digitos; nunca se guarda el codigo en claro. */
    @Column(name = "codigo_hash", nullable = false, length = 64)
    private String codeHash;

    /** VERIFICACION | RESET_PASSWORD */
    @Column(name = "tipo", nullable = false, length = 20)
    private String type;

    @Column(name = "expira_en", nullable = false)
    private Instant expiresAt;

    @Column(name = "intentos", nullable = false)
    private int attempts;

    @Column(name = "usado", nullable = false)
    private boolean used;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant createdAt;
}
