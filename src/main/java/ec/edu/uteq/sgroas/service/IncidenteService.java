package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.IncidenteRequest;
import ec.edu.uteq.sgroas.dto.IncidenteResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.AsignacionRutaRepository;
import ec.edu.uteq.sgroas.repository.IncidenteRepository;
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
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final AsignacionRutaRepository asignacionRutaRepository;

    /**
     * Obtiene la pagina de incidentes activos convertidos a formato de respuesta.
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con los incidentes activos encontrados
     */
    public Page<IncidenteResponse> listar(Pageable pageable) {
        List<IncidenteResponse> contenido = listarCacheable(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    /**
     * Obtiene desde la memoria cache la lista de incidentes activos de la pagina solicitada.
     * @param pageable objeto con numero de pagina y tamanio que identifican la entrada guardada en cache
     * @return lista de incidentes activos correspondientes a la pagina pedida
     */
    @Cacheable(value = "incidentes", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<IncidenteResponse> listarCacheable(Pageable pageable) {
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
    public IncidenteResponse buscarPorId(Long id) {
        Incidente incidente = obtenerIncidenteActivo(id);
        return mapearAResponse(incidente);
    }

    /**
     * Registra un nuevo incidente asociado a una asignacion de ruta vigente.
     * @param request datos del incidente con asignacion, tipo, descripcion, fecha, ubicacion, gravedad y estado
     * @return datos del incidente recien guardado
     * @throws IllegalArgumentException cuando la asignacion no existe o esta inactiva, o tipo, gravedad o estado no son validos
     */
    @CacheEvict(value = "incidentes", allEntries = true)
    public IncidenteResponse crear(IncidenteRequest request) {
        AsignacionRuta asignacion = asignacionRutaRepository.findById(request.asignacionId())
                .filter(AsignacionRuta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        Incidente incidente = Incidente.builder()
                .asignacion(asignacion)
                .reportadoPor(request.reportadoPor())
                .tipo(convertirTipo(request.tipo()))
                .descripcion(request.descripcion())
                .fechaIncidente(request.fechaIncidente())
                .ubicacion(request.ubicacion())
                .gravedad(convertirGravedad(request.gravedad()))
                .estado(convertirEstado(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        Incidente incidenteGuardado = incidenteRepository.save(incidente);
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
    public IncidenteResponse actualizar(Long id, IncidenteRequest request) {
        Incidente incidente = obtenerIncidenteActivo(id);

        AsignacionRuta asignacion = asignacionRutaRepository.findById(request.asignacionId())
                .filter(AsignacionRuta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        incidente.setAsignacion(asignacion);
        incidente.setReportadoPor(request.reportadoPor());
        incidente.setTipo(convertirTipo(request.tipo()));
        incidente.setDescripcion(request.descripcion());
        incidente.setFechaIncidente(request.fechaIncidente());
        incidente.setUbicacion(request.ubicacion());
        incidente.setGravedad(convertirGravedad(request.gravedad()));
        incidente.setEstado(convertirEstado(request.estado()));
        incidente.setActualizadoEn(Instant.now());

        Incidente incidenteActualizado = incidenteRepository.save(incidente);
        return mapearAResponse(incidenteActualizado);
    }

    /**
     * Marca un incidente como inactivo y lo deja en estado cerrado.
     * @param id identificador del incidente que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe un incidente activo con ese identificador
     */
    @CacheEvict(value = "incidentes", allEntries = true)
    public void desactivar(Long id) {
        Incidente incidente = obtenerIncidenteActivo(id);
        incidente.setActivo(false);
        incidente.setEstado(EstadoIncidente.CERRADO);
        incidente.setActualizadoEn(Instant.now());
        incidenteRepository.save(incidente);
    }

    private Incidente obtenerIncidenteActivo(Long id) {
        return incidenteRepository.findById(id)
                .filter(Incidente::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Incidente no encontrado"));
    }

    private TipoIncidente convertirTipo(String tipo) {
        try {
            return TipoIncidente.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de incidente no valido");
        }
    }

    private GravedadIncidente convertirGravedad(String gravedad) {
        try {
            return GravedadIncidente.valueOf(gravedad.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gravedad de incidente no valida");
        }
    }

    private EstadoIncidente convertirEstado(String estado) {
        try {
            return EstadoIncidente.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de incidente no valido");
        }
    }

    private IncidenteResponse mapearAResponse(Incidente incidente) {
        return new IncidenteResponse(
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
