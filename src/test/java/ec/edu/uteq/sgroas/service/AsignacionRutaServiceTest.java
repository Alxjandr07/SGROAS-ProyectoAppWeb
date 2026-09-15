package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.RouteAssignmentRequest;
import ec.edu.uteq.sgroas.dto.RouteAssignmentResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.RouteAssignmentRepository;
import ec.edu.uteq.sgroas.repository.DriverRepository;
import ec.edu.uteq.sgroas.repository.RouteRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionRutaServiceTest {

    @Mock
    private RouteAssignmentRepository asignacionRutaRepository;

    @Mock
    private DriverRepository conductorRepository;

    @Mock
    private VehicleRepository vehiculoRepository;

    @Mock
    private RouteRepository rutaRepository;

    @InjectMocks
    private RouteAssignmentService asignacionRutaService;

    private Driver conductorEjemplo() {
        return Driver.builder()
                .id(1L).firstNames("Carlos").lastNames("Mendoza")
                .nationalId("1200000001").licenseNumber("LIC-001")
                .licenseType("E").licenseExpiry(LocalDate.now().plusDays(30))
                .phone("0988888888").email("carlos@sgroas.com")
                .status(DriverStatus.ACTIVO).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    private Vehicle vehiculoEjemplo() {
        return Vehicle.builder()
                .id(1L).plate("GTU-001").brand("Toyota").model("Hiace")
                .year(2020).capacity(14).engineNumber("MOT")
                .chassisNumber("CHAS").color("Blanco")
                .status(VehicleStatus.ACTIVO).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    private Route rutaEjemplo() {
        return Route.builder()
                .id(1L).code("R-001").name("Quito-Guayaquil")
                .origin("Quito").destination("Guayaquil").distanceKm(420.0)
                .durationMin(480).status(RouteStatus.ACTIVA).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    private RouteAssignment asignacionEjemplo() {
        return RouteAssignment.builder()
                .id(1L).driver(conductorEjemplo()).vehicle(vehiculoEjemplo())
                .route(rutaEjemplo()).assignmentDate(LocalDate.now())
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(1))
                .status(AssignmentStatus.ACTIVA).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    private RouteAssignmentRequest requestEjemplo() {
        return new RouteAssignmentRequest(
                1L, 1L, 1L, LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusDays(1), "ACTIVA"
        );
    }

    @Test
    void listReturnsPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(asignacionRutaRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(asignacionEjemplo())));

        Page<RouteAssignmentResponse> pagina = asignacionRutaService.list(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Carlos Mendoza", pagina.getContent().get(0).driverName());
        assertEquals("GTU-001", pagina.getContent().get(0).vehiclePlate());
        assertEquals("Quito-Guayaquil", pagina.getContent().get(0).routeName());
    }

    @Test
    void findByIdReturnsAssignment() {
        when(asignacionRutaRepository.findWithDetalle(1L))
                .thenReturn(Optional.of(asignacionEjemplo()));

        RouteAssignmentResponse response = asignacionRutaService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("ACTIVA", response.status());
    }

    @Test
    void findByIdNonexistentThrowsException() {
        when(asignacionRutaRepository.findWithDetalle(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.findById(99L));
    }

    @Test
    void createSavesAndReturns() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));
        when(asignacionRutaRepository.save(any(RouteAssignment.class)))
                .thenReturn(asignacionEjemplo());

        RouteAssignmentResponse response = asignacionRutaService.create(requestEjemplo());

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(asignacionRutaRepository).save(any(RouteAssignment.class));
    }

    @Test
    void createWithoutDriverThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void createWithoutVehicleThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void createWithoutRouteThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(rutaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void createWithInvalidStatusThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));

        RouteAssignmentRequest request = new RouteAssignmentRequest(
                1L, 1L, 1L, LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusDays(1), "INVALIDO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(request));
    }

    @Test
    void updateModifiesAndReturns() {
        when(asignacionRutaRepository.findWithDetalle(1L))
                .thenReturn(Optional.of(asignacionEjemplo()));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));
        when(asignacionRutaRepository.save(any(RouteAssignment.class)))
                .thenReturn(asignacionEjemplo());

        RouteAssignmentResponse response = asignacionRutaService.update(1L, requestEjemplo());

        assertEquals(1L, response.id());
        verify(asignacionRutaRepository).save(any(RouteAssignment.class));
    }

    @Test
    void deactivateChangesStatus() {
        when(asignacionRutaRepository.findWithDetalle(1L))
                .thenReturn(Optional.of(asignacionEjemplo()));

        asignacionRutaService.deactivate(1L);

        verify(asignacionRutaRepository).save(argThat(a ->
                !a.getActive() && a.getStatus() == AssignmentStatus.CANCELADA));
    }
}
