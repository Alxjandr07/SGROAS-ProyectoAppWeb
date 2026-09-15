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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConductorServiceTest {

    @Mock
    private DriverRepository conductorRepository;

    @InjectMocks
    private DriverService conductorService;

    private Driver conductorBase() {
        return Driver.builder()
                .id(1L)
                .firstNames("Carlos Alberto")
                .lastNames("Mendoza Vera")
                .nationalId("1200000001")
                .licenseNumber("LIC-001-2026")
                .licenseType("E")
                .licenseExpiry(LocalDate.now().plusDays(20))
                .phone("0988888888")
                .email("carlos.mendoza@sgroas.com")
                .status(DriverStatus.ACTIVO)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private DriverRequest requestBase() {
        return new DriverRequest(
                "Carlos Alberto",
                "Mendoza Vera",
                "1200000001",
                "LIC-001-2026",
                "E",
                LocalDate.now().plusDays(20),
                "0988888888",
                "carlos.mendoza@sgroas.com",
                "ACTIVO"
        );
    }

    @Test
    void createDriverCorrectly() {
        DriverRequest request = new DriverRequest(
                "Carlos Alberto",
                "Mendoza Vera",
                "1200000001",
                "LIC-001-2026",
                "E",
                LocalDate.of(2026, 7, 15),
                "0988888888",
                "carlos.mendoza@sgroas.com",
                "ACTIVO"
        );

        Driver conductorGuardado = Driver.builder()
                .id(1L)
                .firstNames("Carlos Alberto")
                .lastNames("Mendoza Vera")
                .nationalId("1200000001")
                .licenseNumber("LIC-001-2026")
                .licenseType("E")
                .licenseExpiry(LocalDate.of(2026, 7, 15))
                .phone("0988888888")
                .email("carlos.mendoza@sgroas.com")
                .status(DriverStatus.ACTIVO)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(conductorRepository.existsByNationalId("1200000001"))
                .thenReturn(false);
        when(conductorRepository.existsByLicenseNumber("LIC-001-2026"))
                .thenReturn(false);
        when(conductorRepository.save(any(Driver.class)))
                .thenReturn(conductorGuardado);

        DriverResponse response = conductorService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Carlos Alberto", response.firstNames());
        assertEquals("1200000001", response.nationalId());
        assertEquals("ACTIVO", response.status());
        verify(conductorRepository).save(any(Driver.class));
    }

    @Test
    void createDriverWithDuplicateCedulaThrowsException() {
        DriverRequest request = new DriverRequest(
                "Carlos Alberto",
                "Mendoza Vera",
                "1200000001",
                "LIC-001-2026",
                "E",
                LocalDate.of(2026, 7, 15),
                "0988888888",
                "carlos.mendoza@sgroas.com",
                "ACTIVO"
        );

        when(conductorRepository.existsByNationalId("1200000001"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> conductorService.create(request)
        );

        assertEquals("Ya existe un conductor con esa cedula", exception.getMessage());
        verify(conductorRepository, never()).save(any(Driver.class));
    }

    @Test
    void findDriverByIdCorrectly() {
        Driver conductor = Driver.builder()
                .id(1L)
                .firstNames("Carlos Alberto")
                .lastNames("Mendoza Vera")
                .nationalId("1200000001")
                .licenseNumber("LIC-001-2026")
                .licenseType("E")
                .licenseExpiry(LocalDate.now().plusDays(20))
                .phone("0988888888")
                .email("carlos.mendoza@sgroas.com")
                .status(DriverStatus.ACTIVO)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(conductorRepository.findById(1L))
                .thenReturn(Optional.of(conductor));

        DriverResponse response = conductorService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Carlos Alberto", response.firstNames());
        assertTrue(response.licenseExpiring());
    }

    @Test
    void listWithoutSearchUsesFindByActiveTrue() {
        org.springframework.data.domain.PageRequest pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);
        when(conductorRepository.findByActiveTrue(pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(conductorBase())));

        var pagina = conductorService.list(null, pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(conductorRepository).findByActiveTrue(pageable);
        verify(conductorRepository, never()).searchActive(any(), any());
    }

    @Test
    void listWithBlankSearchUsesFindByActiveTrue() {
        org.springframework.data.domain.PageRequest pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);
        when(conductorRepository.findByActiveTrue(pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(conductorBase())));

        var pagina = conductorService.list("   ", pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(conductorRepository).findByActiveTrue(pageable);
    }

    @Test
    void listWithSearchUsesSearchActive() {
        org.springframework.data.domain.PageRequest pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);
        when(conductorRepository.searchActive("carlos", pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(conductorBase())));

        var pagina = conductorService.list("  Carlos ", pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(conductorRepository).searchActive("carlos", pageable);
        verify(conductorRepository, never()).findByActiveTrue(pageable);
    }

    @Test
    void updateWithDuplicateCedulaThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorBase()));
        when(conductorRepository.existsByNationalId("0999999999")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "0999999999", "LIC-001-2026",
                "E", LocalDate.now().plusDays(20), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO");

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.update(1L, request));
    }

    @Test
    void updateWithDuplicateLicenseThrowsException() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorBase()));
        when(conductorRepository.existsByLicenseNumber("LIC-999-2026")).thenReturn(true);

        DriverRequest request = new DriverRequest(
                "Carlos Alberto", "Mendoza Vera", "1200000001", "LIC-999-2026",
                "E", LocalDate.now().plusDays(20), "0988888888",
                "carlos.mendoza@sgroas.com", "ACTIVO");

        assertThrows(IllegalArgumentException.class,
                () -> conductorService.update(1L, request));
    }

    @Test
    void updateModifiesAndSaves() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorBase()));
        when(conductorRepository.save(any(Driver.class))).thenReturn(conductorBase());

        DriverResponse response = conductorService.update(1L, requestBase());

        assertEquals(1L, response.id());
        assertEquals("1200000001", response.nationalId());
        verify(conductorRepository).save(any(Driver.class));
        verify(conductorRepository, never()).existsByNationalId(any());
        verify(conductorRepository, never()).existsByLicenseNumber(any());
    }

    @Test
    void deactivateMarksInactive() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductorBase()));

        conductorService.deactivate(1L);

        verify(conductorRepository).save(argThat(c ->
                !c.getActive() && c.getStatus() == DriverStatus.INACTIVO));
    }

    @Test
    void licenciaVencidaDebeMarcarLicenciaPorVencerFalse() {
        Driver vencido = conductorBase();
        vencido.setLicenseExpiry(LocalDate.now().minusDays(5));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(vencido));

        DriverResponse response = conductorService.findById(1L);

        assertFalse(response.licenseExpiring());
    }

    @Test
    void licenciaLejanaDebeMarcarLicenciaPorVencerFalse() {
        Driver lejana = conductorBase();
        lejana.setLicenseExpiry(LocalDate.now().plusDays(60));
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(lejana));

        DriverResponse response = conductorService.findById(1L);

        assertFalse(response.licenseExpiring());
    }
}
