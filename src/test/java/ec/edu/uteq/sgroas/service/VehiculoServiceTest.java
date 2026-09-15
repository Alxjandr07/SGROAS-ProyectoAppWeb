package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.VehicleRequest;
import ec.edu.uteq.sgroas.dto.VehicleResponse;
import ec.edu.uteq.sgroas.entity.VehicleStatus;
import ec.edu.uteq.sgroas.entity.Vehicle;
import ec.edu.uteq.sgroas.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehicleRepository vehiculoRepository;

    @InjectMocks
    private VehicleService vehiculoService;

    private Vehicle vehiculoEjemplo() {
        return Vehicle.builder()
                .id(1L)
                .plate("GTU-001")
                .brand("Toyota")
                .model("Hiace")
                .year(2020)
                .capacity(14)
                .engineNumber("MOT-123")
                .chassisNumber("CHAS-123")
                .color("Blanco")
                .status(VehicleStatus.ACTIVO)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private VehicleRequest requestEjemplo() {
        return new VehicleRequest(
                "GTU-001", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "ACTIVO"
        );
    }

    @Test
    void listReturnsPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Vehicle vehiculo = vehiculoEjemplo();
        when(vehiculoRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(vehiculo)));

        Page<VehicleResponse> pagina = vehiculoService.list(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("GTU-001", pagina.getContent().get(0).plate());
        assertEquals("ACTIVO", pagina.getContent().get(0).status());
    }

    @Test
    void findByIdReturnsVehicle() {
        when(vehiculoRepository.findById(1L))
                .thenReturn(Optional.of(vehiculoEjemplo()));

        VehicleResponse response = vehiculoService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Toyota", response.brand());
    }

    @Test
    void findByIdNonexistentThrowsException() {
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoService.findById(99L));
    }

    @Test
    void createSavesAndReturns() {
        when(vehiculoRepository.existsByPlate("GTU-001")).thenReturn(false);
        when(vehiculoRepository.save(any(Vehicle.class)))
                .thenReturn(vehiculoEjemplo());

        VehicleResponse response = vehiculoService.create(requestEjemplo());

        assertNotNull(response);
        assertEquals("GTU-001", response.plate());
        verify(vehiculoRepository).save(any(Vehicle.class));
    }

    @Test
    void createWithDuplicatePlateThrowsException() {
        when(vehiculoRepository.existsByPlate("GTU-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoService.create(requestEjemplo()));
        verify(vehiculoRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void createWithInvalidStatusThrowsException() {
        when(vehiculoRepository.existsByPlate("GTU-001")).thenReturn(false);
        VehicleRequest request = new VehicleRequest(
                "GTU-001", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "INVALIDO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoService.create(request));
    }

    @Test
    void updateModifiesAndReturns() {
        when(vehiculoRepository.findById(1L))
                .thenReturn(Optional.of(vehiculoEjemplo()));
        when(vehiculoRepository.save(any(Vehicle.class)))
                .thenReturn(vehiculoEjemplo());

        VehicleResponse response = vehiculoService.update(1L, requestEjemplo());

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(vehiculoRepository).save(any(Vehicle.class));
    }

    @Test
    void updateWithDuplicatePlateThrowsException() {
        Vehicle vehiculo = vehiculoEjemplo();
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.existsByPlate("GTU-999")).thenReturn(true);

        VehicleRequest request = new VehicleRequest(
                "GTU-999", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "ACTIVO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoService.update(1L, request));
    }

    @Test
    void deactivateChangesStatus() {
        when(vehiculoRepository.findById(1L))
                .thenReturn(Optional.of(vehiculoEjemplo()));

        vehiculoService.deactivate(1L);

        verify(vehiculoRepository).save(argThat(v ->
                !v.getActive() && v.getStatus() == VehicleStatus.FUERA_DE_SERVICIO));
    }
}
