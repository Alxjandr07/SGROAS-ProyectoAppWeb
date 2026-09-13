package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.DriverRequest;
import ec.edu.uteq.sgroas.dto.DriverResponse;
import ec.edu.uteq.sgroas.entity.Driver;
import ec.edu.uteq.sgroas.entity.DriverStatus;
import ec.edu.uteq.sgroas.repository.DriverRepository;
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
class ConductorServiceExtraTest {

    @Mock
    private DriverRepository conductorRepository;

    @InjectMocks
    private DriverService conductorService;

    private Driver conductorEjemplo() {
        return Driver.builder()
                .id(1L)
                .nombres("Carlos Alberto")
                .apellidos("Mendoza Vera")
                .cedula("1200000001")
                .numeroLicencia("LIC-001-2026")
                .tipoLicencia("E")
                .fechaVencimientoLicencia(LocalDate.now().plusDays(40))
                .telefono("0988888888")
                .email("carlos.mendoza@sgroas.com")
                .estado(DriverStatus.ACTIVO)
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();
    }

    private DriverRequest requestEjemplo() {
        return new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-001-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO"
        );
    }

    @Test
    void listarDebeRetornarPagina() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(conductorRepository.findByActivoTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(conductorEjemplo())));

        Page<DriverResponse> pagina = conductorService.listar(null, pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Carlos Alberto", pagina.getContent().get(0).nombres());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(conductorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.buscarPorId(99L));
    }

    @Test
    void buscarConductorInactivoDebeLanzarExcepcion() {
        Driver inactivo = conductorEjemplo();
        inactivo.setActivo(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(inactivo));

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.buscarPorId(1L));
    }

    @Test
    void crearConLicenciaDuplicadaDebeLanzarExcepcion() {
        when(conductorRepository.existsByCedula("1200000001")).thenReturn(false);
        when(conductorRepository.existsByNumeroLicencia("LIC-001-2026")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.crear(requestEjemplo()));
    }

    @Test
    void crearConEstadoInvalidoDebeLanzarExcepcion() {
        when(conductorRepository.existsByCedula("1200000001")).thenReturn(false);
        when(conductorRepository.existsByNumeroLicencia("LIC-001-2026")).thenReturn(false);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-001-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "INVALIDO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.crear(request));
    }

    @Test
    void actualizarDebeModificarYRetornar() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.save(any(Driver.class))).thenReturn(conductorEjemplo());

        DriverResponse response = conductorService.actualizar(1L, requestEjemplo());

        assertEquals(1L, response.id());
        verify(conductorRepository).save(any(Driver.class));
    }

    @Test
    void actualizarConCedulaDeOtroDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.existsByCedula("1299999999")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1299999999", "LIC-001-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.actualizar(1L, request));
    }

    @Test
    void actualizarConLicenciaDeOtroDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.existsByNumeroLicencia("LIC-999-2026")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-999-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.actualizar(1L, request));
    }

    @Test
    void desactivarDebeCambiarEstado() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));

        conductorService.desactivar(1L);

        verify(conductorRepository).save(argThat(c ->
                !c.getActivo() && c.getEstado() == DriverStatus.INACTIVO));
    }

    @Test
    void licenciaVencidaDebeMarcarseComoNoPorVencer() {
        Driver conductor = conductorEjemplo();
        conductor.setFechaVencimientoLicencia(LocalDate.now().minusDays(5));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        DriverResponse response = conductorService.buscarPorId(1L);

        assertFalse(response.licenciaPorVencer());
    }
}
