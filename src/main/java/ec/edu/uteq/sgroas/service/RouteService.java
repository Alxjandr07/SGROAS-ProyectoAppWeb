package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.RouteRequest;
import ec.edu.uteq.sgroas.dto.RouteResponse;
import ec.edu.uteq.sgroas.entity.RouteStatus;
import ec.edu.uteq.sgroas.entity.Route;
import ec.edu.uteq.sgroas.repository.RouteRepository;
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
public class RouteService {

    private final RouteRepository rutaRepository;

    public Page<RouteResponse> list(Pageable pageable) {
        List<RouteResponse> contenido = listCached(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    @Cacheable(value = "rutas", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<RouteResponse> listCached(Pageable pageable) {
        return rutaRepository.findByActiveTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    public RouteResponse findById(Long id) {
        Route ruta = getActiveRoute(id);
        return mapearAResponse(ruta);
    }

    @CacheEvict(value = "rutas", allEntries = true)
    public RouteResponse create(RouteRequest request) {
        if (rutaRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ya existe una ruta con ese codigo");
        }

        Route ruta = Route.builder()
                .code(request.code())
                .name(request.name())
                .origin(request.origin())
                .destination(request.destination())
                .distanceKm(request.distanceKm())
                .durationMin(request.durationMin())
                .status(toStatus(request.status()))
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Route rutaGuardada = rutaRepository.save(ruta);
        return mapearAResponse(rutaGuardada);
    }

    @CacheEvict(value = "rutas", allEntries = true)
    public RouteResponse update(Long id, RouteRequest request) {
        Route ruta = getActiveRoute(id);

        if (!ruta.getCode().equals(request.code())
                && rutaRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ya existe una ruta con ese codigo");
        }

        ruta.setCode(request.code());
        ruta.setName(request.name());
        ruta.setOrigin(request.origin());
        ruta.setDestination(request.destination());
        ruta.setDistanceKm(request.distanceKm());
        ruta.setDurationMin(request.durationMin());
        ruta.setStatus(toStatus(request.status()));
        ruta.setUpdatedAt(Instant.now());

        Route rutaActualizada = rutaRepository.save(ruta);
        return mapearAResponse(rutaActualizada);
    }

    @CacheEvict(value = "rutas", allEntries = true)
    public void desactivar(Long id) {
        Route ruta = getActiveRoute(id);
        ruta.setActive(false);
        ruta.setStatus(RouteStatus.INACTIVA);
        ruta.setUpdatedAt(Instant.now());
        rutaRepository.save(ruta);
    }

    private Route getActiveRoute(Long id) {
        return rutaRepository.findById(id)
                .filter(Route::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Route no encontrada"));
    }

    private RouteStatus toStatus(String status) {
        try {
            return RouteStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de ruta no valido");
        }
    }

    private RouteResponse mapearAResponse(Route ruta) {
        return new RouteResponse(
                ruta.getId(),
                ruta.getCode(),
                ruta.getName(),
                ruta.getOrigin(),
                ruta.getDestination(),
                ruta.getDistanceKm(),
                ruta.getDurationMin(),
                ruta.getStatus().name(),
                ruta.getActive(),
                ruta.getCreatedAt(),
                ruta.getUpdatedAt()
        );
    }
}
