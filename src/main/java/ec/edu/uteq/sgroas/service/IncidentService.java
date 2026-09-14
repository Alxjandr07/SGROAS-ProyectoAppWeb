package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.IncidentRequest;
import ec.edu.uteq.sgroas.dto.IncidentResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.RouteAssignmentRepository;
import ec.edu.uteq.sgroas.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidenteRepository;
    private final RouteAssignmentRepository asignacionRutaRepository;

    /**
     * Obtiene la pagina de incidentes activos convertidos a formato de respuesta.
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con los incidentes activos encontrados
     */
    public Page<IncidentResponse> list(Pageable pageable) {
        List<IncidentResponse> contenido = listCached(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    /**
     * Obtiene desde la memoria cache la lista de incidentes activos de la pagina solicitada.
     * @param pageable objeto con numero de pagina y tamanio que identifican la entrada guardada en cache
     * @return lista de incidentes activos correspondientes a la pagina pedida
     */
    @Cacheable(value = "incidentes", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<IncidentResponse> listCached(Pageable pageable) {
        return incidenteRepository.findByActivoTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    /**
     * Recupera el detalle de un incidente activo existente.
     * @param id identificador del incidente que se desea consultar
     * @return datos del incidente encontrado
     * @throws IllegalArgumentException cuando no existe un incidente activo con ese identificador
     */
    public IncidentResponse findById(Long id) {
        Incident incidente = getActiveIncident(id);
        return mapearAResponse(incidente);
    }

    /**
     * Registra un nuevo incidente asociado a una asignacion de ruta vigente.
     * @param request datos del incidente con asignacion, tipo, descripcion, fecha, ubicacion, gravedad y estado
     * @return datos del incidente recien guardado
     * @throws IllegalArgumentException cuando la asignacion no existe o esta inactiva, o tipo, gravedad o estado no son validos
     */
    @CacheEvict(value = "incidentes", allEntries = true)
    public IncidentResponse create(IncidentRequest request) {
        RouteAssignment asignacion = asignacionRutaRepository.findById(request.asignacionId())
                .filter(RouteAssignment::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        Incident incidente = Incident.builder()
                .asignacion(asignacion)
                .reportadoPor(request.reportadoPor())
                .tipo(toType(request.tipo()))
                .descripcion(request.descripcion())
                .fechaIncidente(request.fechaIncidente())
                .ubicacion(request.ubicacion())
                .gravedad(toSeverity(request.gravedad()))
                .estado(toStatus(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        Incident incidenteGuardado = incidenteRepository.save(incidente);
        return mapearAResponse(incidenteGuardado);
    }

    /**
     * Reemplaza los datos de un incidente activo por los valores recibidos.
     * @param id identificador del incidente que se desea modificar
     * @param request nuevos datos del incidente con asignacion, tipo, descripcion, fecha, ubicacion, gravedad y estado
     * @return datos del incidente ya actualizado
     * @throws IllegalArgumentException cuando el incidente o la asignacion no existe o esta inactivo, o tipo, gravedad o estado no son validos
     */
    @CacheEvict(value = "incidentes", allEntries = true)
    public IncidentResponse update(Long id, IncidentRequest request) {
        Incident incidente = getActiveIncident(id);

        RouteAssignment asignacion = asignacionRutaRepository.findById(request.asignacionId())
                .filter(RouteAssignment::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        incidente.setAsignacion(asignacion);
        incidente.setReportadoPor(request.reportadoPor());
        incidente.setTipo(toType(request.tipo()));
        incidente.setDescripcion(request.descripcion());
        incidente.setFechaIncidente(request.fechaIncidente());
        incidente.setUbicacion(request.ubicacion());
        incidente.setGravedad(toSeverity(request.gravedad()));
        incidente.setEstado(toStatus(request.estado()));
        incidente.setActualizadoEn(Instant.now());

        Incident incidenteActualizado = incidenteRepository.save(incidente);
        return mapearAResponse(incidenteActualizado);
    }

    /**
     * Marca un incidente como inactivo y lo deja en estado cerrado.
     * @param id identificador del incidente que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe un incidente activo con ese identificador
     */
    @CacheEvict(value = "incidentes", allEntries = true)
    public void desactivar(Long id) {
        Incident incidente = getActiveIncident(id);
        incidente.setActivo(false);
        incidente.setEstado(IncidentStatus.CERRADO);
        incidente.setActualizadoEn(Instant.now());
        incidenteRepository.save(incidente);
    }

    private Incident getActiveIncident(Long id) {
        return incidenteRepository.findById(id)
                .filter(Incident::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Incident no encontrado"));
    }

    private IncidentType toType(String tipo) {
        try {
            return IncidentType.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de incidente no valido");
        }
    }

    private IncidentSeverity toSeverity(String gravedad) {
        try {
            return IncidentSeverity.valueOf(gravedad.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gravedad de incidente no valida");
        }
    }

    private IncidentStatus toStatus(String estado) {
        try {
            return IncidentStatus.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de incidente no valido");
        }
    }

    private IncidentResponse mapearAResponse(Incident incidente) {
        return new IncidentResponse(
                incidente.getId(),
                incidente.getAsignacion().getId(),
                incidente.getReportadoPor(),
                incidente.getTipo().name(),
                incidente.getDescripcion(),
                incidente.getFechaIncidente(),
                incidente.getUbicacion(),
                incidente.getGravedad().name(),
                incidente.getEstado().name(),
                incidente.getActivo(),
                incidente.getCreadoEn(),
                incidente.getActualizadoEn()
        );
    }
}
