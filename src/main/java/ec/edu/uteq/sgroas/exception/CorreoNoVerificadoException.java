package ec.edu.uteq.sgroas.exception;

/** La cuenta existe pero aun no confirma el codigo enviado a su correo. */
public class CorreoNoVerificadoException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje que explica la falta de verificación.
     * @param mensaje texto que describe que la cuenta requiere verificar el correo.
     */
    public CorreoNoVerificadoException(String mensaje) {
        super(mensaje);
    }
}
