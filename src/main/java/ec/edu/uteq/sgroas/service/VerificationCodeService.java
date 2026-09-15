package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.entity.VerificationCode;
import ec.edu.uteq.sgroas.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

/**
 * Generacion y validacion de codigos de 6 digitos de un solo uso.
 * - Validez: 10 minutos.
 * - Maximo 5 intentos por codigo.
 * - Reenvio con espera minima de 60 segundos.
 * - Solo se almacena el hash SHA-256 del codigo.
 */
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    public enum Type { VERIFICACION, RESET_PASSWORD }

    private static final Duration VALIDEZ = Duration.ofMinutes(10);
    private static final Duration ESPERA_REENVIO = Duration.ofSeconds(60);
    private static final int MAX_INTENTOS = 5;

    private static final String CODIGO_INVALIDO = "El codigo es invalido o ya expiro";

    private final VerificationCodeRepository repository;
    private final SecureRandom aleatorio = new SecureRandom();

    @Transactional
    public String generate(String email, Type type) {
        repository.deleteByEmailAndType(email, type.name());

        String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
        repository.save(VerificationCode.builder()
                .email(email)
                .codeHash(sha256(codigo))
                .type(type.name())
                .expiresAt(Instant.now().plus(VALIDEZ))
                .attempts(0)
                .used(false)
                .createdAt(Instant.now())
                .build());
        return codigo;
    }

    public boolean canResend(String email, Type type) {
        return repository.findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type.name())
                .map(c -> c.getCreatedAt().isBefore(Instant.now().minus(ESPERA_REENVIO)))
                .orElse(true);
    }

    @Transactional
    public void validate(String email, Type type, String codigo) {
        VerificationCode registro = repository
                .findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type.name())
                .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));

        if (registro.isUsed() || registro.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        if (registro.getAttempts() >= MAX_INTENTOS) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        if (!registro.getCodeHash().equals(sha256(codigo))) {
            registro.setAttempts(registro.getAttempts() + 1);
            repository.save(registro);
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        registro.setUsed(true);
        repository.save(registro);
    }

    private String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
