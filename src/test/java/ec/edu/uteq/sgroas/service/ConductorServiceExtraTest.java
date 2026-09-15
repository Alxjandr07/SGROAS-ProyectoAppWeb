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
                .firstNames("Carlos Alberto")
                .lastNames("Mendoza Vera")
                .nationalId("1200000001")
                .licenseNumber("LIC-001-2026")
                .licenseType("E")
                .licenseExpiry(LocalDate.now().plusDays(40))
                .phone("0988888888")
                .email("carlos.mendoza@sgroas.com")
                .status(DriverStatus.ACTIVO)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
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
        when(conductorRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(conductorEjemplo())));

        Page<DriverResponse> pagina = conductorService.list(null, pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Carlos Alberto", pagina.getContent().get(0).firstNames());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(conductorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.findById(99L));
    }

    @Test
    void buscarConductorInactivoDebeLanzarExcepcion() {
        Driver inactivo = conductorEjemplo();
        inactivo.setActive(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(inactivo));

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.findById(1L));
    }

    @Test
    void crearConLicenciaDuplicadaDebeLanzarExcepcion() {
        when(conductorRepository.existsByNationalId("1200000001")).thenReturn(false);
        when(conductorRepository.existsByLicenseNumber("LIC-001-2026")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.create(requestEjemplo()));
    }

    @Test
    void crearConEstadoInvalidoDebeLanzarExcepcion() {
        when(conductorRepository.existsByNationalId("1200000001")).thenReturn(false);
        when(conductorRepository.existsByLicenseNumber("LIC-001-2026")).thenReturn(false);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-001-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "INVALIDO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.create(request));
    }

    @Test
    void actualizarDebeModificarYRetornar() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.save(any(Driver.class))).thenReturn(conductorEjemplo());

        DriverResponse response = conductorService.update(1L, requestEjemplo());

        assertEquals(1L, response.id());
        verify(conductorRepository).save(any(Driver.class));
    }

    @Test
    void actualizarConCedulaDeOtroDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.existsByNationalId("1299999999")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1299999999", "LIC-001-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.update(1L, request));
    }

    @Test
    void actualizarConLicenciaDeOtroDebeLanzarExcepcion() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));
        when(conductorRepository.existsByLicenseNumber("LIC-999-2026")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-999-2026",
                "E", LocalDate.of(2026, 7, 15), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO"
        );

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.update(1L, request));
    }

    @Test
    void desactivarDebeCambiarEstado() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorEjemplo()));

        conductorService.desactivar(1L);

        verify(conductorRepository).save(argThat(c ->
                !c.getActive() && c.getStatus() == DriverStatus.INACTIVO));
    }

    @Test
    void licenciaVencidaDebeMarcarseComoNoPorVencer() {
        Driver conductor = conductorEjemplo();
        conductor.setLicenseExpiry(LocalDate.now().minusDays(5));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        DriverResponse response = conductorService.findById(1L);

        assertFalse(response.licenseExpiring());
    }
}
