package ec.edu.uteq.sgroas.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void erroresDeValidacionDebenRetornarProblemDetail() {
        when(request.getRequestURI()).thenReturn("/api/conductores");
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("conductor", "cedula", "La cedula es obligatoria")
        ));
        MethodArgumentNotValidException ex =
                org.mockito.Mockito.mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail detail = handler.manejarErroresValidacion(ex, request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), detail.getStatus());
        assertEquals("Error de validacion", detail.getTitle());
        @SuppressWarnings("unchecked")
        Map<String, String> errores = (Map<String, String>) detail.getProperties().get("errors");
        assertEquals("La cedula es obligatoria", errores.get("cedula"));
    }

    @Test
    void argumentosInvalidosDebenRetornarBadRequest() {
        when(request.getRequestURI()).thenReturn("/api/conductores");

        ProblemDetail detail = handler.manejarArgumentosInvalidos(
                new IllegalArgumentException("Conductor no encontrado"), request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), detail.getStatus());
        assertEquals("Conductor no encontrado", detail.getDetail());
    }

    @Test
    void credencialesInvalidasDebenRetornarNoAutorizado() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ProblemDetail detail = handler.manejarCredencialesInvalidas(
                new BadCredentialsException("Credenciales invalidas"), request);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), detail.getStatus());
        assertEquals("Credenciales invalidas", detail.getTitle());
    }

    @Test
    void rutaInexistenteDebeRetornarNoEncontrado() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        ProblemDetail detail = handler.manejarRutaInexistente(
                new org.springframework.web.servlet.resource.NoResourceFoundException(
                        null, "api/auth/register"),
                request);

        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
        assertEquals("Recurso no encontrado", detail.getTitle());
    }

    @Test
    void errorGeneralDebeRetornarInternalServerError() {
        when(request.getRequestURI()).thenReturn("/api/conductores");

        ProblemDetail detail = handler.manejarErrorGeneral(
                new RuntimeException("Falla inesperada"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertTrue(detail.getDetail().contains("Falla inesperada"));
    }
}
