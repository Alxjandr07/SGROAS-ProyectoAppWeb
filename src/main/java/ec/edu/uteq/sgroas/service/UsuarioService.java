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

    /**
     * Obtiene la pagina de usuarios activos, con filtro opcional por texto de busqueda.
     * @param search texto opcional para filtrar por nombre o correo, nulo o vacio para traer todo
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con los usuarios activos encontrados
     */
    public Page<UsuarioResponse> listar(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return usuarioRepository.findByActivoTrue(pageable).map(this::toResponse);
        }
        return usuarioRepository.buscarActivos(search.trim().toLowerCase(), pageable)
                .map(this::toResponse);
    }

    /**
     * Recupera el detalle de un usuario existente por su identificador.
     * @param id identificador del usuario que se desea consultar
     * @return datos del usuario encontrado
     * @throws EntityNotFoundException cuando no existe un usuario con ese identificador
     */
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    /**
     * Crea el usuario y le envia por correo un codigo de activacion de seis digitos.
     * La cuenta nace sin verificar y no podra iniciar sesion hasta confirmar el codigo recibido.
     * @param request datos del usuario con nombre, correo, contrasenia y rol asignado
     * @return datos del usuario recien guardado
     * @throws IllegalArgumentException cuando ya existe otro usuario con el mismo correo
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

    /**
     * Reenvia el codigo de activacion a una cuenta que aun no verifica su correo.
     * El administrador usa esta accion cuando el usuario perdio o no recibio el primer codigo.
     * @param id identificador del usuario pendiente de verificacion que recibira el codigo
     * @throws EntityNotFoundException cuando no existe un usuario con ese identificador
     * @throws IllegalArgumentException cuando la cuenta ya verifico su correo o se pidio un codigo hace menos de un minuto
     */
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

    /**
     * Reemplaza los datos basicos de un usuario por los valores recibidos.
     * Solo cambia la contrasenia cuando se envia una nueva no vacia.
     * @param id identificador del usuario que se desea modificar
     * @param request nuevos datos del usuario con nombre, correo, rol y contrasenia opcional
     * @return datos del usuario ya actualizado
     * @throws EntityNotFoundException cuando no existe un usuario con ese identificador
     */
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

    /**
     * Marca un usuario como inactivo para impedir su acceso futuro al sistema.
     * @param id identificador del usuario que se desea dar de baja
     * @throws EntityNotFoundException cuando no existe un usuario con ese identificador
     */
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
