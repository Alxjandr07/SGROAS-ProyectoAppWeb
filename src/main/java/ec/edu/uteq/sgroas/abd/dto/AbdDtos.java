package ec.edu.uteq.sgroas.abd.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTOs de la capa ABD. Records inmutables con validacion Jakarta.
 */
public final class AbdDtos {

    private AbdDtos() {
    }

    // ---------- Catalogos ----------

    public record ProvinciaResponse(Integer idProvincia, String nombre) {
    }

    public record CiudadResponse(Integer idCiudad, String nombre, Integer idProvincia, String nombreProvincia) {
    }

    public record TerminalResponse(Integer idTerminal, String nombre, Integer idCiudad, String nombreCiudad) {
    }

    public record RolResponse(Integer idRol, String nombre, String descripcion) {
    }

    public record CatalogosResponse(List<ProvinciaResponse> provincias,
                                    List<CiudadResponse> ciudades,
                                    List<TerminalResponse> terminales,
                                    List<RolResponse> roles) {
    }

    // ---------- Rutas (ABD) ----------

    public record RutaAbdRequest(
            @NotNull(message = "El terminal de origen es obligatorio") Integer idTerminalOrigen,
            @NotNull(message = "El terminal de destino es obligatorio") Integer idTerminalDestino,
            @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
            @Digits(integer = 8, fraction = 2) BigDecimal precioPasaje
    ) {
    }

    public record RutaAbdResponse(Integer idRuta, Integer idTerminalOrigen, String terminalOrigen,
                                  Integer idTerminalDestino, String terminalDestino,
                                  BigDecimal precioPasaje, long totalProgramaciones) {
    }

    // ---------- Unidades ----------

    public record UnidadRequest(
            @NotBlank @Size(max = 15) String placa,
            @NotBlank @Size(max = 10) String numeroDisco,
            @NotBlank @Size(max = 50) String modelo,
            @NotNull @Min(1) @Max(200) Integer capacidad,
            @Min(1950) @Max(2100) Integer anioFabricacion,
            @Pattern(regexp = "Activo|Inactivo|En Mantenimiento", message = "Estado invalido") String estado
    ) {
    }

    public record UnidadResponse(Integer idUnidad, String placa, String numeroDisco, String modelo,
                                 Integer capacidad, Integer anioFabricacion, String estado) {
    }

    // ---------- Programaciones ----------

    public record ProgramacionRequest(
            @NotNull LocalDate fecha,
            @NotNull LocalTime horaSalida,
            @NotNull LocalTime horaEstimadaLlegada,
            @Pattern(regexp = "Programado|En Curso|Completado|Cancelado", message = "Estado invalido") String estado,
            @NotNull Integer idRuta,
            @NotNull Integer idUnidad,
            @NotNull Integer idConductor
    ) {
    }

    public record ProgramacionResponse(Integer idProgramacion, LocalDate fecha, LocalTime horaSalida,
                                       LocalTime horaEstimadaLlegada, String estado,
                                       Integer idRuta, String rutaDescripcion,
                                       Integer idUnidad, String unidadPlaca,
                                       Integer idConductor, String conductorNombres) {
    }

    // ---------- Incidentes (ABD) ----------

    public record IncidenteAbdRequest(
            @NotBlank @Size(max = 50) String tipo,
            @NotBlank String descripcion,
            @NotBlank @Pattern(regexp = "BAJO|MEDIO|ALTO", message = "Nivel sugerido debe ser BAJO, MEDIO o ALTO") String nivelSugerido,
            @Size(max = 255) String evidencia,
            @Pattern(regexp = "Reportado|En Revision|Cerrado", message = "Estado invalido") String estado,
            @NotNull Integer idUnidad
    ) {
    }

    public record IncidenteAbdResponse(Integer idIncidente, String tipo, String descripcion,
                                       String nivelSugerido, String fechaIncidente, String evidencia,
                                       String estado, Integer idUnidad, String unidadPlaca) {
    }

    public record AlertaResponse(Integer idAlerta, String nivelRiesgo, String descripcion,
                                 String fecha, Integer idIncidente, String incidenteTipo) {
    }

    // ---------- Reportes ----------

    public record ConteoResponse(String clave, long total) {
    }

    public record TopRutaResponse(Integer idRuta, String descripcion, long totalProgramaciones) {
    }

    public record ResumenResponse(long totalProgramaciones, long programacionesActivas,
                                  long totalIncidentes, long incidentesAltoNivel,
                                  long totalAlertas, long totalUnidades, long unidadesEnMantenimiento,
                                  long totalRutas) {
    }
}
