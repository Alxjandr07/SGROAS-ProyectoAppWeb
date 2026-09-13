package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.UserRequest;
import ec.edu.uteq.sgroas.dto.UserResponse;
import ec.edu.uteq.sgroas.entity.Role;
import ec.edu.uteq.sgroas.entity.User;
import ec.edu.uteq.sgroas.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UserRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationCodeService codigoVerificacionService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService usuarioService;

    private User usuarioEjemplo() {
        return User.builder()
                .id(1L)
                .nombre("Carlos Mendoza")
                .email("carlos@sgroas.com")
                .passwordHash("hash")
                .rol(Role.ROLE_ADMIN)
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();
    }

    private UserRequest requestEjemplo() {
        return new UserRequest("Carlos Mendoza", "carlos@sgroas.com", "123456", "ROLE_ADMIN");
    }

    @Test
    void listarDebeRetornarPagina() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(usuarioRepository.findByActivoTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(usuarioEjemplo())));

        Page<UserResponse> pagina = usuarioService.listar(null, pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("ROLE_ADMIN", pagina.getContent().get(0).rol());
    }

    @Test
    void listarConBusquedaDebeUsarBuscarActivos() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(usuarioRepository.buscarActivos("carlos", pageable))
                .thenReturn(new PageImpl<>(List.of(usuarioEjemplo())));

        Page<UserResponse> pagina = usuarioService.listar("  Carlos  ", pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(usuarioRepository).buscarActivos("carlos", pageable);
        verify(usuarioRepository, never()).findByActivoTrue(pageable);
    }

    @Test
    void listarConBusquedaEnBlancoDebeUsarFindByActivoTrue() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(usuarioRepository.findByActivoTrue(pageable))
                .thenReturn(new PageImpl<>(List.of(usuarioEjemplo())));

        Page<UserResponse> pagina = usuarioService.listar("   ", pageable);

        assertEquals(1, pagina.getTotalElements());
        verify(usuarioRepository).findByActivoTrue(pageable);
        verify(usuarioRepository, never()).buscarActivos(any(), any());
    }

    @Test
    void buscarPorIdDebeRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));

        UserResponse response = usuarioService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("carlos@sgroas.com", response.email());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.buscarPorId(99L));
    }

    @Test
    void crearDebeGuardarSinVerificarYEnviarCodigoActivacion() {
        when(usuarioRepository.existsByEmail("carlos@sgroas.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash-encrypted");
        when(usuarioRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(codigoVerificacionService.generar("carlos@sgroas.com",
                VerificationCodeService.Tipo.VERIFICACION)).thenReturn("123456");

        UserResponse response = usuarioService.crear(requestEjemplo());

        assertEquals("carlos@sgroas.com", response.email());
        verify(usuarioRepository).save(argThat(u ->
                Boolean.FALSE.equals(u.getVerificado()) && Boolean.TRUE.equals(u.getActivo())));
        verify(emailService).enviarCodigoVerificacion(
                "carlos@sgroas.com", "Carlos Mendoza", "123456");
    }

    @Test
    void crearConEmailDuplicadoDebeLanzarExcepcion() {
        when(usuarioRepository.existsByEmail("carlos@sgroas.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(requestEjemplo()));
    }

    @Test
    void reenviarActivacionDebeEnviarNuevoCodigo() {
        User sinVerificar = usuarioEjemplo();
        sinVerificar.setVerificado(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(sinVerificar));
        when(codigoVerificacionService.puedeReenviar("carlos@sgroas.com",
                VerificationCodeService.Tipo.VERIFICACION)).thenReturn(true);
        when(codigoVerificacionService.generar("carlos@sgroas.com",
                VerificationCodeService.Tipo.VERIFICACION)).thenReturn("654321");

        usuarioService.reenviarCodigoActivacion(1L);

        verify(emailService).enviarCodigoVerificacion(
                "carlos@sgroas.com", "Carlos Mendoza", "654321");
    }

    @Test
    void reenviarActivacionConCuentaYaVerificadaDebeLanzarExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.reenviarCodigoActivacion(1L));
    }

    @Test
    void reenviarActivacionDentroDeLaEsperaDebeLanzarExcepcion() {
        User sinVerificar = usuarioEjemplo();
        sinVerificar.setVerificado(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(sinVerificar));
        when(codigoVerificacionService.puedeReenviar("carlos@sgroas.com",
                VerificationCodeService.Tipo.VERIFICACION)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.reenviarCodigoActivacion(1L));
        verify(emailService, never()).enviarCodigoVerificacion(any(), any(), any());
    }

    @Test
    void reenviarActivacionConUsuarioInexistenteDebeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.reenviarCodigoActivacion(99L));
    }

    @Test
    void actualizarDebeModificarYRetornar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));
        when(usuarioRepository.save(any(User.class))).thenReturn(usuarioEjemplo());

        UserResponse response = usuarioService.actualizar(1L, requestEjemplo());

        assertEquals(1L, response.id());
        verify(usuarioRepository).save(any(User.class));
    }

    @Test
    void actualizarSinPasswordDebeMantenerPasswordHash() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));
        when(usuarioRepository.save(any(User.class))).thenReturn(usuarioEjemplo());

        UserRequest request = new UserRequest(
                "Carlos Mendoza", "carlos@sgroas.com", null, "ROLE_ADMIN");

        UserResponse response = usuarioService.actualizar(1L, request);

        assertEquals("Carlos Mendoza", response.nombre());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarConPasswordEnBlancoDebeMantenerPasswordHash() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));
        when(usuarioRepository.save(any(User.class))).thenReturn(usuarioEjemplo());

        UserRequest request = new UserRequest(
                "Carlos Mendoza", "carlos@sgroas.com", "   ", "ROLE_ADMIN");

        UserResponse response = usuarioService.actualizar(1L, request);

        assertEquals("Carlos Mendoza", response.nombre());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarInexistenteDebeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.actualizar(99L, requestEjemplo()));
    }

    @Test
    void desactivarDebeMarcarInactivo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo()));

        usuarioService.desactivar(1L);

        verify(usuarioRepository).save(argThat(u -> !u.getActivo()));
    }

    @Test
    void desactivarInexistenteDebeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> usuarioService.desactivar(99L));
    }
}
