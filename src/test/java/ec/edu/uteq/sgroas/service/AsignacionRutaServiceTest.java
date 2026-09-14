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
                .id(1L).nombres("Carlos").apellidos("Mendoza")
                .cedula("1200000001").numeroLicencia("LIC-001")
                .tipoLicencia("E").fechaVencimientoLicencia(LocalDate.now().plusDays(30))
                .telefono("0988888888").email("carlos@sgroas.com")
                .estado(DriverStatus.ACTIVO).activo(true)
                .creadoEn(Instant.now()).actualizadoEn(Instant.now())
                .build();
    }

    private Vehicle vehiculoEjemplo() {
        return Vehicle.builder()
                .id(1L).placa("GTU-001").marca("Toyota").modelo("Hiace")
                .anio(2020).capacidadPasajeros(14).numeroMotor("MOT")
                .numeroChasis("CHAS").color("Blanco")
                .estado(VehicleStatus.ACTIVO).activo(true)
                .creadoEn(Instant.now()).actualizadoEn(Instant.now())
                .build();
    }

    private Route rutaEjemplo() {
        return Route.builder()
                .id(1L).codigo("R-001").nombre("Quito-Guayaquil")
                .origen("Quito").destino("Guayaquil").distanciaKm(420.0)
                .duracionEstimadaMin(480).estado(RouteStatus.ACTIVA).activo(true)
                .creadoEn(Instant.now()).actualizadoEn(Instant.now())
                .build();
    }

    private RouteAssignment asignacionEjemplo() {
        return RouteAssignment.builder()
                .id(1L).conductor(conductorEjemplo()).vehiculo(vehiculoEjemplo())
                .ruta(rutaEjemplo()).fechaAsignacion(LocalDate.now())
                .fechaInicio(LocalDate.now()).fechaFin(LocalDate.now().plusDays(1))
                .estado(AssignmentStatus.ACTIVA).activo(true)
                .creadoEn(Instant.now()).actualizadoEn(Instant.now())
                .build();
    }

    private RouteAssignmentRequest requestEjemplo() {
        return new RouteAssignmentRequest(
                1L, 1L, 1L, LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusDays(1), "ACTIVA"
        );
    }

    @Test
    void listarDebeRetornarPagina() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(asignacionRutaRepository.findByActivoTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(asignacionEjemplo())));

        Page<RouteAssignmentResponse> pagina = asignacionRutaService.list(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Carlos Mendoza", pagina.getContent().get(0).conductorNombre());
        assertEquals("GTU-001", pagina.getContent().get(0).vehiculoPlaca());
        assertEquals("Quito-Guayaquil", pagina.getContent().get(0).rutaNombre());
    }

    @Test
    void buscarPorIdDebeRetornarAsignacion() {
        when(asignacionRutaRepository.findWithDetalle(1L))
                .thenReturn(Optional.of(asignacionEjemplo()));

        RouteAssignmentResponse response = asignacionRutaService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("ACTIVA", response.estado());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(asignacionRutaRepository.findWithDetalle(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.findById(99L));
    }

    @Test
    void crearDebeGuardarYRetornar() {
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
    void crearSinConductorDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void crearSinVehiculoDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void crearSinRutaDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoEjemplo()));
        when(rutaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionRutaService.create(requestEjemplo()));
    }

    @Test
    void crearConEstadoInvalidoDebeLanzarExcepcion() {
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
    void actualizarDebeModificarYRetornar() {
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
    void desactivarDebeCambiarEstado() {
        when(asignacionRutaRepository.findWithDetalle(1L))
                .thenReturn(Optional.of(asignacionEjemplo()));

        asignacionRutaService.desactivar(1L);

        verify(asignacionRutaRepository).save(argThat(a ->
                !a.getActivo() && a.getEstado() == AssignmentStatus.CANCELADA));
    }
}
