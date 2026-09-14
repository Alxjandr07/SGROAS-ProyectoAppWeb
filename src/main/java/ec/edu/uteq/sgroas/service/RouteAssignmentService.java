package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.RouteAssignmentRequest;
import ec.edu.uteq.sgroas.dto.RouteAssignmentResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.RouteAssignmentRepository;
import ec.edu.uteq.sgroas.repository.DriverRepository;
import ec.edu.uteq.sgroas.repository.RouteRepository;
import ec.edu.uteq.sgroas.repository.VehicleRepository;
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
public class RouteAssignmentService {

    private final RouteAssignmentRepository asignacionRutaRepository;
    private final DriverRepository conductorRepository;
    private final VehicleRepository vehiculoRepository;
    private final RouteRepository rutaRepository;

    /**
     * Obtiene la pagina de asignaciones activas convertidas a formato de respuesta.
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con las asignaciones activas encontradas
     */
    @Transactional(readOnly = true)
    public Page<RouteAssignmentResponse> list(Pageable pageable) {
        Page<RouteAssignment> page = asignacionRutaRepository.findByActivoTrue(pageable);
        List<RouteAssignmentResponse> contenido = page.map(this::mapearAResponse).getContent();
        return new PageImpl<>(contenido, pageable, page.getTotalElements());
    }

    /**
     * Obtiene desde la memoria cache la lista de asignaciones activas de la pagina solicitada.
     * @param pageable objeto con numero de pagina y tamanio que identifican la entrada guardada en cache
     * @return lista de asignaciones activas correspondientes a la pagina pedida
     */
    @Cacheable(value = "asignaciones", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public List<RouteAssignmentResponse> listCached(Pageable pageable) {
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
    public RouteAssignmentResponse findById(Long id) {
        RouteAssignment asignacion = getActiveAssignment(id);
        return mapearAResponse(asignacion);
    }

    /**
     * Registra una nueva asignacion vinculando un conductor, un vehiculo y una ruta vigentes.
     * @param request datos de la asignacion con participantes, fechas y estado deseado
     * @return datos de la asignacion recien guardada
     * @throws IllegalArgumentException cuando algun participante no existe o esta inactivo, o el estado no es valido
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public RouteAssignmentResponse create(RouteAssignmentRequest request) {
        Driver conductor = conductorRepository.findById(request.conductorId())
                .filter(Driver::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Driver no encontrado"));

        Vehicle vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .filter(Vehicle::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle no encontrado"));

        Route ruta = rutaRepository.findById(request.rutaId())
                .filter(Route::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Route no encontrada"));

        RouteAssignment asignacion = RouteAssignment.builder()
                .conductor(conductor)
                .vehiculo(vehiculo)
                .ruta(ruta)
                .fechaAsignacion(request.fechaAsignacion())
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .estado(toStatus(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        RouteAssignment asignacionGuardada = asignacionRutaRepository.save(asignacion);
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
    public RouteAssignmentResponse update(Long id, RouteAssignmentRequest request) {
        RouteAssignment asignacion = getActiveAssignment(id);

        Driver conductor = conductorRepository.findById(request.conductorId())
                .filter(Driver::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Driver no encontrado"));

        Vehicle vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .filter(Vehicle::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle no encontrado"));

        Route ruta = rutaRepository.findById(request.rutaId())
                .filter(Route::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Route no encontrada"));

        asignacion.setConductor(conductor);
        asignacion.setVehiculo(vehiculo);
        asignacion.setRuta(ruta);
        asignacion.setFechaAsignacion(request.fechaAsignacion());
        asignacion.setFechaInicio(request.fechaInicio());
        asignacion.setFechaFin(request.fechaFin());
        asignacion.setEstado(toStatus(request.estado()));
        asignacion.setActualizadoEn(Instant.now());

        RouteAssignment asignacionActualizada = asignacionRutaRepository.save(asignacion);
        return mapearAResponse(asignacionActualizada);
    }

    /**
     * Marca una asignacion como inactiva y la deja en estado cancelada.
     * @param id identificador de la asignacion que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe una asignacion activa con ese identificador
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public void desactivar(Long id) {
        RouteAssignment asignacion = getActiveAssignment(id);
        asignacion.setActivo(false);
        asignacion.setEstado(AssignmentStatus.CANCELADA);
        asignacion.setActualizadoEn(Instant.now());
        asignacionRutaRepository.save(asignacion);
    }

    private RouteAssignment getActiveAssignment(Long id) {
        return asignacionRutaRepository.findWithDetalle(id)
                .filter(RouteAssignment::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));
    }

    private AssignmentStatus toStatus(String estado) {
        try {
            return AssignmentStatus.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de asignacion no valido");
        }
    }

    private RouteAssignmentResponse mapearAResponse(RouteAssignment asignacion) {
        return new RouteAssignmentResponse(
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
