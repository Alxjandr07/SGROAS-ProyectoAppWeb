package ec.edu.uteq.sgroas.exception;

/** La cuenta existe pero aun no confirma el codigo enviado a su correo. */
public class CorreoNoVerificadoException extends RuntimeException {

    public CorreoNoVerificadoException(String mensaje) {
        super(mensaje);
    }
}
