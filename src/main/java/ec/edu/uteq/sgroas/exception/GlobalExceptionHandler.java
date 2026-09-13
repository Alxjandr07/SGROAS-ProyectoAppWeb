package ec.edu.uteq.sgroas.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Construye la respuesta de error cuando la validación de la solicitud falla.
     * @param ex excepción con el resultado de la validación y los campos inválidos.
     * @param request petición HTTP que originó el error de validación.
     * @return detalle del problema con el mapa de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarErroresValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/validation"));
        detail.setTitle("Error de validacion");
        detail.setDetail("Existen campos invalidos en la solicitud");
        detail.setInstance(URI.create(request.getRequestURI()));

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        detail.setProperty("errors", errores);

        return detail;
    }

    /**
     * Construye la respuesta de error cuando se recibe un argumento inválido.
     * @param ex excepción con el mensaje que explica el argumento rechazado.
     * @param request petición HTTP que originó el argumento inválido.
     * @return detalle del problema con estado de solicitud inválida.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail manejarArgumentosInvalidos(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/bad-request"));
        detail.setTitle("Solicitud invalida");
        detail.setDetail(ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }

    /**
     * Construye la respuesta de error cuando las credenciales de acceso son incorrectas.
     * @param ex excepción de autenticación lanzada por el proveedor de seguridad.
     * @param request petición HTTP que originó el intento fallido de autenticación.
     * @return detalle del problema con estado de no autorizado.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail manejarCredencialesInvalidas(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/unauthorized"));
        detail.setTitle("Credenciales invalidas");
        detail.setDetail("El email o la contrasena son incorrectos");
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }

    /**
     * Construye la respuesta de error cuando la cuenta aún no verifica su correo.
     * @param ex excepción que indica que falta la verificación del correo.
     * @param request petición HTTP que originó el acceso con correo sin verificar.
     * @return detalle del problema con estado de acceso prohibido.
     */
    @ExceptionHandler(CorreoNoVerificadoException.class)
    public ProblemDetail manejarCorreoNoVerificado(
            CorreoNoVerificadoException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/email-not-verified"));
        detail.setTitle("Correo sin verificar");
        detail.setDetail(ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }

    /**
     * Construye la respuesta de error cuando el usuario no tiene los permisos requeridos.
     * @param ex excepción de acceso denegado lanzada por la verificación de roles.
     * @param request petición HTTP que originó el intento sin permisos suficientes.
     * @return detalle del problema con estado de acceso prohibido.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail manejarAccesoDenegado(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/forbidden"));
        detail.setTitle("Acceso denegado");
        detail.setDetail("No tiene permisos para realizar esta operacion");
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }

    /**
     * Construye la respuesta de error cuando la ruta solicitada no existe.
     * @param ex excepción que indica que no se encontró el recurso solicitado.
     * @param request petición HTTP con la dirección inexistente que se intentó acceder.
     * @return detalle del problema con estado de recurso no encontrado.
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ProblemDetail manejarRutaInexistente(
            org.springframework.web.servlet.resource.NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/not-found"));
        detail.setTitle("Recurso no encontrado");
        detail.setDetail("La ruta solicitada no existe");
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }

    /**
     * Construye la respuesta genérica cuando ocurre un error no controlado.
     * @param ex excepción no prevista que provocó el error interno.
     * @param request petición HTTP que originó el error interno del servidor.
     * @return detalle del problema con estado de error interno del servidor.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        ex.printStackTrace();

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setType(URI.create("https://sgroas.uteq.edu.ec/errors/internal"));
        detail.setTitle("Error interno del servidor");
        detail.setDetail(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));

        return detail;
    }
}
