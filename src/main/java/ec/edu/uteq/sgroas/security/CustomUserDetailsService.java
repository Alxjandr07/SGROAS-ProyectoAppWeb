package ec.edu.uteq.sgroas.security;

import ec.edu.uteq.sgroas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository usuarioRepository;

    /**
     * Carga los datos de seguridad del usuario activo para el proceso de autenticacion.
     * @param email correo con el que el usuario intenta identificarse
     * @return datos con correo, clave y autoridad del rol para validar credenciales
     * @throws UsernameNotFoundException cuando no existe el correo o el usuario esta inactivo
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ec.edu.uteq.sgroas.entity.User usuario = usuarioRepository.findByEmail(email)
                .filter(ec.edu.uteq.sgroas.entity.User::getActivo)
                .orElseThrow(() -> new UsernameNotFoundException("User no encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(usuario.getRol().name()))
        );
    }
}