package ec.edu.uteq.sgroas.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void errorResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        ErrorResponse response = new ErrorResponse(
                ahora, 400, "Bad Request", "Mensaje", "/api/test",
                Map.of("campo", "error")
        );

        assertEquals(ahora, response.timestamp());
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Mensaje", response.message());
        assertEquals("/api/test", response.path());
        assertEquals("error", response.details().get("campo"));
        assertEquals(response, new ErrorResponse(
                ahora, 400, "Bad Request", "Mensaje", "/api/test",
                Map.of("campo", "error")));
        assertEquals(response.hashCode(), new ErrorResponse(
                ahora, 400, "Bad Request", "Mensaje", "/api/test",
                Map.of("campo", "error")).hashCode());
        assertTrue(response.toString().contains("Mensaje"));
    }

    @Test
    void emailRequestDebeConservarValores() {
        EmailRequest request = new EmailRequest("maria@sgroas.com");

        assertEquals("maria@sgroas.com", request.email());
    }

    @Test
    void verificarEmailRequestDebeConservarValores() {
        VerificarEmailRequest request = new VerificarEmailRequest(
                "maria@sgroas.com", "123456"
        );

        assertEquals("maria@sgroas.com", request.email());
        assertEquals("123456", request.codigo());
    }

    @Test
    void restablecerContrasenaRequestDebeConservarValores() {
        RestablecerContrasenaRequest request = new RestablecerContrasenaRequest(
                "maria@sgroas.com", "654321", "nuevaClave1"
        );

        assertEquals("maria@sgroas.com", request.email());
        assertEquals("654321", request.codigo());
        assertEquals("nuevaClave1", request.nuevaPassword());
    }

    @Test
    void refreshTokenRequestDebeConservarValores() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

        assertEquals("refresh-token", request.refreshToken());
    }

    @Test
    void usuarioRequestDebeConservarValores() {
        UsuarioRequest request = new UsuarioRequest(
                "Carlos Mendoza", "carlos@sgroas.com", "123456", "ROLE_ADMIN"
        );

        assertEquals("Carlos Mendoza", request.nombre());
        assertEquals("carlos@sgroas.com", request.email());
        assertEquals("123456", request.password());
        assertEquals("ROLE_ADMIN", request.rol());
    }

    @Test
    void usuarioResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        UsuarioResponse response = new UsuarioResponse(
                1L, "Carlos Mendoza", "carlos@sgroas.com",
                "ROLE_ADMIN", true, ahora, ahora
        );

        assertEquals(1L, response.id());
        assertEquals("Carlos Mendoza", response.nombre());
        assertEquals("carlos@sgroas.com", response.email());
        assertEquals("ROLE_ADMIN", response.rol());
        assertTrue(response.activo());
        assertEquals(ahora, response.creadoEn());
        assertEquals(ahora, response.actualizadoEn());
    }

    @Test
    void vehiculoRequestDebeConservarValores() {
        VehiculoRequest request = new VehiculoRequest(
                "GTU-001", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "ACTIVO"
        );

        assertEquals("GTU-001", request.placa());
        assertEquals("Toyota", request.marca());
        assertEquals("Hiace", request.modelo());
        assertEquals(2020, request.anio());
        assertEquals(14, request.capacidadPasajeros());
        assertEquals("MOT-123", request.numeroMotor());
        assertEquals("CHAS-123", request.numeroChasis());
        assertEquals("Blanco", request.color());
        assertEquals("ACTIVO", request.estado());
    }

    @Test
    void vehiculoResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        VehiculoResponse response = new VehiculoResponse(
                1L, "GTU-001", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "ACTIVO", true, ahora, ahora
        );

        assertEquals(1L, response.id());
        assertEquals("GTU-001", response.placa());
        assertEquals("Toyota", response.marca());
        assertEquals("Hiace", response.modelo());
        assertEquals(2020, response.anio());
        assertEquals(14, response.capacidadPasajeros());
        assertEquals("MOT-123", response.numeroMotor());
        assertEquals("CHAS-123", response.numeroChasis());
        assertEquals("Blanco", response.color());
        assertEquals("ACTIVO", response.estado());
        assertTrue(response.activo());
    }

    @Test
    void rutaRequestDebeConservarValores() {
        RutaRequest request = new RutaRequest(
                "R-001", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "ACTIVA"
        );

        assertEquals("R-001", request.codigo());
        assertEquals("Quito - Guayaquil", request.nombre());
        assertEquals("Quito", request.origen());
        assertEquals("Guayaquil", request.destino());
        assertEquals(420.0, request.distanciaKm());
        assertEquals(480, request.duracionEstimadaMin());
        assertEquals("ACTIVA", request.estado());
    }

    @Test
    void rutaResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        RutaResponse response = new RutaResponse(
                1L, "R-001", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "ACTIVA", true, ahora, ahora
        );

        assertEquals(1L, response.id());
        assertEquals("R-001", response.codigo());
        assertEquals("Quito - Guayaquil", response.nombre());
        assertEquals("Quito", response.origen());
        assertEquals("Guayaquil", response.destino());
        assertEquals(420.0, response.distanciaKm());
        assertEquals(480, response.duracionEstimadaMin());
        assertEquals("ACTIVA", response.estado());
        assertTrue(response.activo());
    }

    @Test
    void incidenteRequestDebeConservarValores() {
        LocalDateTime fecha = LocalDateTime.now();
        IncidenteRequest request = new IncidenteRequest(
                1L, "Carlos Mendoza", "AVERIA_MECANICA", "Falla en el motor",
                fecha, "Km 12", "MEDIA", "REPORTADO"
        );

        assertEquals(1L, request.asignacionId());
        assertEquals("Carlos Mendoza", request.reportadoPor());
        assertEquals("AVERIA_MECANICA", request.tipo());
        assertEquals("Falla en el motor", request.descripcion());
        assertEquals(fecha, request.fechaIncidente());
        assertEquals("Km 12", request.ubicacion());
        assertEquals("MEDIA", request.gravedad());
        assertEquals("REPORTADO", request.estado());
    }

    @Test
    void incidenteResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        LocalDateTime fecha = LocalDateTime.now();
        IncidenteResponse response = new IncidenteResponse(
                1L, 1L, "Carlos Mendoza", "AVERIA_MECANICA",
                "Falla en el motor", fecha, "Km 12", "MEDIA",
                "REPORTADO", true, ahora, ahora
        );

        assertEquals(1L, response.id());
        assertEquals(1L, response.asignacionId());
        assertEquals("Carlos Mendoza", response.reportadoPor());
        assertEquals("AVERIA_MECANICA", response.tipo());
        assertEquals("Falla en el motor", response.descripcion());
        assertEquals(fecha, response.fechaIncidente());
        assertEquals("Km 12", response.ubicacion());
        assertEquals("MEDIA", response.gravedad());
        assertEquals("REPORTADO", response.estado());
        assertTrue(response.activo());
    }

    @Test
    void asignacionRutaRequestDebeConservarValores() {
        LocalDate fecha = LocalDate.now();
        AsignacionRutaRequest request = new AsignacionRutaRequest(
                1L, 1L, 1L, fecha, fecha, fecha.plusDays(1), "ACTIVA"
        );

        assertEquals(1L, request.conductorId());
        assertEquals(1L, request.vehiculoId());
        assertEquals(1L, request.rutaId());
        assertEquals(fecha, request.fechaAsignacion());
        assertEquals(fecha, request.fechaInicio());
        assertEquals(fecha.plusDays(1), request.fechaFin());
        assertEquals("ACTIVA", request.estado());
    }

    @Test
    void asignacionRutaResponseDebeConservarValores() {
        Instant ahora = Instant.now();
        LocalDate fecha = LocalDate.now();
        AsignacionRutaResponse response = new AsignacionRutaResponse(
                1L, 1L, "Carlos Mendoza", 1L, "GTU-001", 1L,
                "Quito - Guayaquil", fecha, fecha, fecha.plusDays(1),
                "ACTIVA", true, ahora, ahora
        );

        assertEquals(1L, response.id());
        assertEquals(1L, response.conductorId());
        assertEquals("Carlos Mendoza", response.conductorNombre());
        assertEquals(1L, response.vehiculoId());
        assertEquals("GTU-001", response.vehiculoPlaca());
        assertEquals(1L, response.rutaId());
        assertEquals("Quito - Guayaquil", response.rutaNombre());
        assertEquals(fecha, response.fechaAsignacion());
        assertEquals(fecha, response.fechaInicio());
        assertEquals(fecha.plusDays(1), response.fechaFin());
        assertEquals("ACTIVA", response.estado());
        assertTrue(response.activo());
    }
}
