package ec.edu.uteq.sgroas.security;

import ec.edu.uteq.sgroas.entity.Role;
import ec.edu.uteq.sgroas.entity.User;
import ec.edu.uteq.sgroas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameDebeRetornarUsuario() {
        User usuario = User.builder()
                .id(1L)
                .nombre("Administrador SGROAS")
                .email("admin@sgroas.com")
                .passwordHash("password-encriptado")
                .rol(Role.ROLE_ADMIN)
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@sgroas.com");

        assertEquals("admin@sgroas.com", userDetails.getUsername());
        assertEquals("password-encriptado", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsernameConEmailInexistenteDebeLanzarExcepcion() {
        when(usuarioRepository.findByEmail("desconocido@sgroas.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("desconocido@sgroas.com"));
    }

    @Test
    void loadUserByUsernameConUsuarioInactivoDebeLanzarExcepcion() {
        User inactivo = User.builder()
                .id(1L)
                .nombre("Administrador")
                .email("admin@sgroas.com")
                .passwordHash("hash")
                .rol(Role.ROLE_ADMIN)
                .activo(false)
                .build();
        when(usuarioRepository.findByEmail("admin@sgroas.com"))
                .thenReturn(Optional.of(inactivo));

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("admin@sgroas.com"));
    }
}
