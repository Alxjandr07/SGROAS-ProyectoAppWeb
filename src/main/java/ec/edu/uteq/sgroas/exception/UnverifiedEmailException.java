package ec.edu.uteq.sgroas.exception;

/** La cuenta existe pero aun no confirma el codigo enviado a su correo. */
public class UnverifiedEmailException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje que explica la falta de verificación.
     * @param mensaje texto que describe que la cuenta requiere verificar el correo.
     */
    public UnverifiedEmailException(String mensaje) {
        super(mensaje);
    }
}
