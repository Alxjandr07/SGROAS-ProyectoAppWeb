package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.VehicleRequest;
import ec.edu.uteq.sgroas.dto.VehicleResponse;
import ec.edu.uteq.sgroas.entity.VehicleStatus;
import ec.edu.uteq.sgroas.entity.Vehicle;
import ec.edu.uteq.sgroas.repository.VehicleRepository;
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
public class VehicleService {

    private final VehicleRepository vehiculoRepository;

    public Page<VehicleResponse> list(Pageable pageable) {
        List<VehicleResponse> contenido = listCached(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    @Cacheable(value = "vehiculos", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<VehicleResponse> listCached(Pageable pageable) {
        return vehiculoRepository.findByActiveTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    public VehicleResponse findById(Long id) {
        Vehicle vehiculo = getActiveVehicle(id);
        return mapearAResponse(vehiculo);
    }

    @CacheEvict(value = "vehiculos", allEntries = true)
    public VehicleResponse create(VehicleRequest request) {
        if (vehiculoRepository.existsByPlate(request.plate())) {
            throw new IllegalArgumentException("Ya existe un vehiculo con esa placa");
        }

        Vehicle vehiculo = Vehicle.builder()
                .plate(request.plate())
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .capacity(request.capacity())
                .engineNumber(request.engineNumber())
                .chassisNumber(request.chassisNumber())
                .color(request.color())
                .status(toStatus(request.status()))
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Vehicle vehiculoGuardado = vehiculoRepository.save(vehiculo);
        return mapearAResponse(vehiculoGuardado);
    }

    @CacheEvict(value = "vehiculos", allEntries = true)
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehiculo = getActiveVehicle(id);

        if (!vehiculo.getPlate().equals(request.plate())
                && vehiculoRepository.existsByPlate(request.plate())) {
            throw new IllegalArgumentException("Ya existe un vehiculo con esa placa");
        }

        vehiculo.setPlate(request.plate());
        vehiculo.setBrand(request.brand());
        vehiculo.setModel(request.model());
        vehiculo.setYear(request.year());
        vehiculo.setCapacity(request.capacity());
        vehiculo.setEngineNumber(request.engineNumber());
        vehiculo.setChassisNumber(request.chassisNumber());
        vehiculo.setColor(request.color());
        vehiculo.setStatus(toStatus(request.status()));
        vehiculo.setUpdatedAt(Instant.now());

        Vehicle vehiculoActualizado = vehiculoRepository.save(vehiculo);
        return mapearAResponse(vehiculoActualizado);
    }

    @CacheEvict(value = "vehiculos", allEntries = true)
    public void desactivar(Long id) {
        Vehicle vehiculo = getActiveVehicle(id);
        vehiculo.setActive(false);
        vehiculo.setStatus(VehicleStatus.FUERA_DE_SERVICIO);
        vehiculo.setUpdatedAt(Instant.now());
        vehiculoRepository.save(vehiculo);
    }

    private Vehicle getActiveVehicle(Long id) {
        return vehiculoRepository.findById(id)
                .filter(Vehicle::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle no encontrado"));
    }

    private VehicleStatus toStatus(String status) {
        try {
            return VehicleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de vehiculo no valido");
        }
    }

    private VehicleResponse mapearAResponse(Vehicle vehiculo) {
        return new VehicleResponse(
                vehiculo.getId(),
                vehiculo.getPlate(),
                vehiculo.getBrand(),
                vehiculo.getModel(),
                vehiculo.getYear(),
                vehiculo.getCapacity(),
                vehiculo.getEngineNumber(),
                vehiculo.getChassisNumber(),
                vehiculo.getColor(),
                vehiculo.getStatus().name(),
                vehiculo.getActive(),
                vehiculo.getCreatedAt(),
                vehiculo.getUpdatedAt()
        );
    }
}
