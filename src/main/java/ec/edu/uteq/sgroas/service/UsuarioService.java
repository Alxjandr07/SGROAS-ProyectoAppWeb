package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.UsuarioRequest;
import ec.edu.uteq.sgroas.dto.UsuarioResponse;
import ec.edu.uteq.sgroas.entity.Rol;
import ec.edu.uteq.sgroas.entity.Usuario;
import ec.edu.uteq.sgroas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacionService codigoVerificacionService;
    private final EmailService emailService;

    public Page<UsuarioResponse> listar(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return usuarioRepository.findByActivoTrue(pageable).map(this::toResponse);
        }
        return usuarioRepository.buscarActivos(search.trim().toLowerCase(), pageable)
                .map(this::toResponse);
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    /**
     * Crea el usuario y le envia por correo un codigo de activacion de 6 digitos.
     * La cuenta nace con verificado = false: no podra iniciar sesion hasta
     * confirmar el codigo en la pantalla de activacion del login.
     */
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(Rol.valueOf(request.rol().toUpperCase()))
                .activo(true)
                .verificado(false)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        enviarCodigoActivacion(guardado);

        return toResponse(guardado);
    }

    /** El ADMIN puede reenviar el codigo de activacion de una cuenta sin verificar. */
    public void reenviarCodigoActivacion(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        if (Boolean.TRUE.equals(usuario.getVerificado())) {
            throw new IllegalArgumentException("Ese usuario ya verifico su correo");
        }
        if (!codigoVerificacionService.puedeReenviar(usuario.getEmail(),
                CodigoVerificacionService.Tipo.VERIFICACION)) {
            throw new IllegalArgumentException(
                    "El codigo se envio hace menos de un minuto. Espera antes de reenviar.");
        }
        enviarCodigoActivacion(usuario);
    }

    private void enviarCodigoActivacion(Usuario usuario) {
        String codigo = codigoVerificacionService.generar(usuario.getEmail(),
                CodigoVerificacionService.Tipo.VERIFICACION);
        emailService.enviarCodigoVerificacion(usuario.getEmail(), usuario.getNombre(), codigo);
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setRol(Rol.valueOf(request.rol().toUpperCase()));
        usuario.setActualizadoEn(Instant.now());

        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuario.setActualizadoEn(Instant.now());
        usuarioRepository.save(usuario);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNombre(), u.getEmail(),
                u.getRol().name(), u.getActivo(),
                u.getCreadoEn(), u.getActualizadoEn()
        );
    }
}
