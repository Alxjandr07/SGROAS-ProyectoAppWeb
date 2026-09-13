package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.AsignacionRutaRequest;
import ec.edu.uteq.sgroas.dto.AsignacionRutaResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.AsignacionRutaRepository;
import ec.edu.uteq.sgroas.repository.ConductorRepository;
import ec.edu.uteq.sgroas.repository.RutaRepository;
import ec.edu.uteq.sgroas.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionRutaService {

    private final AsignacionRutaRepository asignacionRutaRepository;
    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;

    /**
     * Obtiene la pagina de asignaciones activas convertidas a formato de respuesta.
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con las asignaciones activas encontradas
     */
    @Transactional(readOnly = true)
    public Page<AsignacionRutaResponse> listar(Pageable pageable) {
        Page<AsignacionRuta> page = asignacionRutaRepository.findByActivoTrue(pageable);
        List<AsignacionRutaResponse> contenido = page.map(this::mapearAResponse).getContent();
        return new PageImpl<>(contenido, pageable, page.getTotalElements());
    }

    /**
     * Obtiene desde la memoria cache la lista de asignaciones activas de la pagina solicitada.
     * @param pageable objeto con numero de pagina y tamanio que identifican la entrada guardada en cache
     * @return lista de asignaciones activas correspondientes a la pagina pedida
     */
    @Cacheable(value = "asignaciones", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public List<AsignacionRutaResponse> listarCacheable(Pageable pageable) {
        return asignacionRutaRepository.findByActivoTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    /**
     * Recupera el detalle de una asignacion activa existente.
     * @param id identificador de la asignacion que se desea consultar
     * @return datos de la asignacion encontrada
     * @throws IllegalArgumentException cuando no existe una asignacion activa con ese identificador
     */
    @Transactional(readOnly = true)
    public AsignacionRutaResponse buscarPorId(Long id) {
        AsignacionRuta asignacion = obtenerAsignacionActiva(id);
        return mapearAResponse(asignacion);
    }

    /**
     * Registra una nueva asignacion vinculando un conductor, un vehiculo y una ruta vigentes.
     * @param request datos de la asignacion con participantes, fechas y estado deseado
     * @return datos de la asignacion recien guardada
     * @throws IllegalArgumentException cuando algun participante no existe o esta inactivo, o el estado no es valido
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public AsignacionRutaResponse crear(AsignacionRutaRequest request) {
        Conductor conductor = conductorRepository.findById(request.conductorId())
                .filter(Conductor::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado"));

        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .filter(Vehiculo::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado"));

        Ruta ruta = rutaRepository.findById(request.rutaId())
                .filter(Ruta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        AsignacionRuta asignacion = AsignacionRuta.builder()
                .conductor(conductor)
                .vehiculo(vehiculo)
                .ruta(ruta)
                .fechaAsignacion(request.fechaAsignacion())
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .estado(convertirEstado(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        AsignacionRuta asignacionGuardada = asignacionRutaRepository.save(asignacion);
        return mapearAResponse(asignacionGuardada);
    }

    /**
     * Reemplaza los datos de una asignacion activa por los valores recibidos.
     * @param id identificador de la asignacion que se desea modificar
     * @param request nuevos datos de participantes, fechas y estado para la asignacion
     * @return datos de la asignacion ya actualizada
     * @throws IllegalArgumentException cuando la asignacion o algun participante no existe o esta inactivo, o el estado no es valido
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public AsignacionRutaResponse actualizar(Long id, AsignacionRutaRequest request) {
        AsignacionRuta asignacion = obtenerAsignacionActiva(id);

        Conductor conductor = conductorRepository.findById(request.conductorId())
                .filter(Conductor::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado"));

        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .filter(Vehiculo::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado"));

        Ruta ruta = rutaRepository.findById(request.rutaId())
                .filter(Ruta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        asignacion.setConductor(conductor);
        asignacion.setVehiculo(vehiculo);
        asignacion.setRuta(ruta);
        asignacion.setFechaAsignacion(request.fechaAsignacion());
        asignacion.setFechaInicio(request.fechaInicio());
        asignacion.setFechaFin(request.fechaFin());
        asignacion.setEstado(convertirEstado(request.estado()));
        asignacion.setActualizadoEn(Instant.now());

        AsignacionRuta asignacionActualizada = asignacionRutaRepository.save(asignacion);
        return mapearAResponse(asignacionActualizada);
    }

    /**
     * Marca una asignacion como inactiva y la deja en estado cancelada.
     * @param id identificador de la asignacion que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe una asignacion activa con ese identificador
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public void desactivar(Long id) {
        AsignacionRuta asignacion = obtenerAsignacionActiva(id);
        asignacion.setActivo(false);
        asignacion.setEstado(EstadoAsignacion.CANCELADA);
        asignacion.setActualizadoEn(Instant.now());
        asignacionRutaRepository.save(asignacion);
    }

    private AsignacionRuta obtenerAsignacionActiva(Long id) {
        return asignacionRutaRepository.findWithDetalle(id)
                .filter(AsignacionRuta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));
    }

    private EstadoAsignacion convertirEstado(String estado) {
        try {
            return EstadoAsignacion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de asignacion no valido");
        }
    }

    private AsignacionRutaResponse mapearAResponse(AsignacionRuta asignacion) {
        return new AsignacionRutaResponse(
                asignacion.getId(),
                asignacion.getConductor().getId(),
                asignacion.getConductor().getNombres() + " " + asignacion.getConductor().getApellidos(),
                asignacion.getVehiculo().getId(),
                asignacion.getVehiculo().getPlaca(),
                asignacion.getRuta().getId(),
                asignacion.getRuta().getNombre(),
                asignacion.getFechaAsignacion(),
                asignacion.getFechaInicio(),
                asignacion.getFechaFin(),
                asignacion.getEstado().name(),
                asignacion.getActivo(),
                asignacion.getCreadoEn(),
                asignacion.getActualizadoEn()
        );
    }
}
