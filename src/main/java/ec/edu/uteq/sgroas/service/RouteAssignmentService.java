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
     * Returns a paginated list of active route assignments.
     * @param pageable pagination and sorting configuration.
     * @return page of assignment response records.
     */
    @Transactional(readOnly = true)
    public Page<RouteAssignmentResponse> list(Pageable pageable) {
        Page<RouteAssignment> page = asignacionRutaRepository.findByActiveTrue(pageable);
        List<RouteAssignmentResponse> contenido = page.map(this::mapearAResponse).getContent();
        return new PageImpl<>(contenido, pageable, page.getTotalElements());
    }

    /**
     * Returns the cached list of active assignments for the given pageable, keyed by page.
     * @param pageable pagination and sorting configuration.
     * @return list of assignment response records.
     */
    @Cacheable(value = "asignaciones", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public List<RouteAssignmentResponse> listCached(Pageable pageable) {
        return asignacionRutaRepository.findByActiveTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    /**
     * Retrieves a route assignment by its unique identifier.
     * @param id assignment unique identifier.
     * @return the matching assignment response.
     */
    @Transactional(readOnly = true)
    public RouteAssignmentResponse findById(Long id) {
        RouteAssignment asignacion = getActiveAssignment(id);
        return mapearAResponse(asignacion);
    }

    /**
     * Registers a new route assignment. Caches are evicted after creation.
     * @param request assignment data to register.
     * @return the created assignment response.
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public RouteAssignmentResponse create(RouteAssignmentRequest request) {
        Driver conductor = conductorRepository.findById(request.driverId())
                .filter(Driver::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Driver no encontrado"));

        Vehicle vehiculo = vehiculoRepository.findById(request.vehicleId())
                .filter(Vehicle::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle no encontrado"));

        Route ruta = rutaRepository.findById(request.routeId())
                .filter(Route::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Route no encontrada"));

        RouteAssignment asignacion = RouteAssignment.builder()
                .driver(conductor)
                .vehicle(vehiculo)
                .route(ruta)
                .assignmentDate(request.assignmentDate())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(toStatus(request.status()))
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        RouteAssignment asignacionGuardada = asignacionRutaRepository.save(asignacion);
        return mapearAResponse(asignacionGuardada);
    }

    /**
     * Updates an existing active route assignment. Caches are evicted after update.
     * @param id assignment unique identifier.
     * @param request new assignment data.
     * @return the updated assignment response.
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public RouteAssignmentResponse update(Long id, RouteAssignmentRequest request) {
        RouteAssignment asignacion = getActiveAssignment(id);

        Driver conductor = conductorRepository.findById(request.driverId())
                .filter(Driver::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Driver no encontrado"));

        Vehicle vehiculo = vehiculoRepository.findById(request.vehicleId())
                .filter(Vehicle::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle no encontrado"));

        Route ruta = rutaRepository.findById(request.routeId())
                .filter(Route::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Route no encontrada"));

        asignacion.setDriver(conductor);
        asignacion.setVehicle(vehiculo);
        asignacion.setRoute(ruta);
        asignacion.setAssignmentDate(request.assignmentDate());
        asignacion.setStartDate(request.startDate());
        asignacion.setEndDate(request.endDate());
        asignacion.setStatus(toStatus(request.status()));
        asignacion.setUpdatedAt(Instant.now());

        RouteAssignment asignacionActualizada = asignacionRutaRepository.save(asignacion);
        return mapearAResponse(asignacionActualizada);
    }

    /**
     * Logically deactivates a route assignment. Caches are evicted after deactivation.
     * @param id routeassignment unique identifier.
     */
    @CacheEvict(value = "asignaciones", allEntries = true)
    public void deactivate(Long id) {
        RouteAssignment asignacion = getActiveAssignment(id);
        asignacion.setActive(false);
        asignacion.setStatus(AssignmentStatus.CANCELADA);
        asignacion.setUpdatedAt(Instant.now());
        asignacionRutaRepository.save(asignacion);
    }

    private RouteAssignment getActiveAssignment(Long id) {
        return asignacionRutaRepository.findWithDetalle(id)
                .filter(RouteAssignment::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));
    }

    private AssignmentStatus toStatus(String status) {
        try {
            return AssignmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de asignacion no valido");
        }
    }

    private RouteAssignmentResponse mapearAResponse(RouteAssignment asignacion) {
        return new RouteAssignmentResponse(
                asignacion.getId(),
                asignacion.getDriver().getId(),
                asignacion.getDriver().getFirstNames() + " " + asignacion.getDriver().getLastNames(),
                asignacion.getVehicle().getId(),
                asignacion.getVehicle().getPlate(),
                asignacion.getRoute().getId(),
                asignacion.getRoute().getName(),
                asignacion.getAssignmentDate(),
                asignacion.getStartDate(),
                asignacion.getEndDate(),
                asignacion.getStatus().name(),
                asignacion.getActive(),
                asignacion.getCreatedAt(),
                asignacion.getUpdatedAt()
        );
    }
}
