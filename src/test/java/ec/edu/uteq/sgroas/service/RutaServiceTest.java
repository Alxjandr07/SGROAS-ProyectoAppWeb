package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.RouteRequest;
import ec.edu.uteq.sgroas.dto.RouteResponse;
import ec.edu.uteq.sgroas.entity.RouteStatus;
import ec.edu.uteq.sgroas.entity.Route;
import ec.edu.uteq.sgroas.repository.RouteRepository;
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
class RutaServiceTest {

    @Mock
    private RouteRepository rutaRepository;

    @InjectMocks
    private RouteService rutaService;

    private Route rutaEjemplo() {
        return Route.builder()
                .id(1L)
                .code("R-001")
                .name("Quito - Guayaquil")
                .origin("Quito")
                .destination("Guayaquil")
                .distanceKm(420.0)
                .durationMin(480)
                .status(RouteStatus.ACTIVA)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private RouteRequest requestEjemplo() {
        return new RouteRequest(
                "R-001", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "ACTIVA"
        );
    }

    @Test
    void listarDebeRetornarPagina() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(rutaRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(rutaEjemplo())));

        Page<RouteResponse> pagina = rutaService.list(pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("R-001", pagina.getContent().get(0).code());
    }

    @Test
    void buscarPorIdDebeRetornarRuta() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));

        RouteResponse response = rutaService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("Quito", response.origin());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> rutaService.findById(99L));
    }

    @Test
    void crearDebeGuardarYRetornar() {
        when(rutaRepository.existsByCode("R-001")).thenReturn(false);
        when(rutaRepository.save(any(Route.class))).thenReturn(rutaEjemplo());

        RouteResponse response = rutaService.create(requestEjemplo());

        assertEquals("R-001", response.code());
        verify(rutaRepository).save(any(Route.class));
    }

    @Test
    void crearConCodigoDuplicadoDebeLanzarExcepcion() {
        when(rutaRepository.existsByCode("R-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> rutaService.create(requestEjemplo()));
    }

    @Test
    void crearConEstadoInvalidoDebeLanzarExcepcion() {
        when(rutaRepository.existsByCode("R-001")).thenReturn(false);
        RouteRequest request = new RouteRequest(
                "R-001", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "INVALIDO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> rutaService.create(request));
    }

    @Test
    void actualizarDebeModificarYRetornar() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));
        when(rutaRepository.save(any(Route.class))).thenReturn(rutaEjemplo());

        RouteResponse response = rutaService.update(1L, requestEjemplo());

        assertEquals(1L, response.id());
    }

    @Test
    void actualizarConCodigoDuplicadoDebeLanzarExcepcion() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));
        when(rutaRepository.existsByCode("R-999")).thenReturn(true);

        RouteRequest request = new RouteRequest(
                "R-999", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "ACTIVA"
        );

        assertThrows(IllegalArgumentException.class,
                () -> rutaService.update(1L, request));
    }

    @Test
    void desactivarDebeCambiarEstado() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(rutaEjemplo()));

        rutaService.desactivar(1L);

        verify(rutaRepository).save(argThat(r ->
                !r.getActive() && r.getStatus() == RouteStatus.INACTIVA));
    }
}
