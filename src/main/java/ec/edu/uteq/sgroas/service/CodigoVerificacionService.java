package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.entity.CodigoVerificacion;
import ec.edu.uteq.sgroas.repository.CodigoVerificacionRepository;
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
public class CodigoVerificacionService {

    public enum Tipo { VERIFICACION, RESET_PASSWORD }

    private static final Duration VALIDEZ = Duration.ofMinutes(10);
    private static final Duration ESPERA_REENVIO = Duration.ofSeconds(60);
    private static final int MAX_INTENTOS = 5;

    private static final String CODIGO_INVALIDO = "El codigo es invalido o ya expiro";

    private final CodigoVerificacionRepository repository;
    private final SecureRandom aleatorio = new SecureRandom();

    /**
     * Genera un codigo nuevo de seis digitos, invalida los anteriores del mismo tipo y lo devuelve en claro.
     * Guarda solo el resumen cifrado y concede diez minutos de vigencia para su uso.
     * @param email correo del usuario al que pertenece el codigo por generar
     * @param tipo proposito del codigo, activacion de cuenta o restablecimiento de contrasena
     * @return codigo de seis digitos en claro listo para enviarse por correo
     */
    @Transactional
    public String generar(String email, Tipo tipo) {
        repository.deleteByEmailAndTipo(email, tipo.name());

        String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
        repository.save(CodigoVerificacion.builder()
                .email(email)
                .codigoHash(sha256(codigo))
                .tipo(tipo.name())
                .expiraEn(Instant.now().plus(VALIDEZ))
                .intentos(0)
                .usado(false)
                .creadoEn(Instant.now())
                .build());
        return codigo;
    }

    /**
     * Indica si ya transcurrio la espera minima de sesenta segundos para pedir otro codigo.
     * @param email correo del usuario que desea saber si puede solicitar un reenvio
     * @param tipo proposito del codigo, activacion de cuenta o restablecimiento de contrasena
     * @return verdadero cuando puede generarse otro codigo, falso cuando aun debe esperar
     */
    public boolean puedeReenviar(String email, Tipo tipo) {
        return repository.findFirstByEmailAndTipoOrderByCreadoEnDesc(email, tipo.name())
                .map(c -> c.getCreadoEn().isBefore(Instant.now().minus(ESPERA_REENVIO)))
                .orElse(true);
    }

    /**
     * Valida el codigo recibido contra el ultimo emitido y lo marca como usado si coincide.
     * Cuenta cada intento fallido y bloquea el codigo tras cinco errores o al vencer su vigencia.
     * @param email correo del usuario propietario del codigo por validar
     * @param tipo proposito del codigo, activacion de cuenta o restablecimiento de contrasena
     * @param codigo codigo de seis digitos ingresado por el usuario para su comprobacion
     * @throws IllegalArgumentException cuando no existe codigo, ya fue usado, expiro, supero los intentos o no coincide
     */
    @Transactional
    public void validar(String email, Tipo tipo, String codigo) {
        CodigoVerificacion registro = repository
                .findFirstByEmailAndTipoOrderByCreadoEnDesc(email, tipo.name())
                .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));

        if (registro.isUsado() || registro.getExpiraEn().isBefore(Instant.now())) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        if (registro.getIntentos() >= MAX_INTENTOS) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        if (!registro.getCodigoHash().equals(sha256(codigo))) {
            registro.setIntentos(registro.getIntentos() + 1);
            repository.save(registro);
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        registro.setUsado(true);
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
