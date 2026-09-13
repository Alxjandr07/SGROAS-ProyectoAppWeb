package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.ConductorRequest;
import ec.edu.uteq.sgroas.dto.ConductorResponse;
import ec.edu.uteq.sgroas.entity.Conductor;
import ec.edu.uteq.sgroas.entity.EstadoConductor;
import ec.edu.uteq.sgroas.repository.ConductorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConductorService {

    private final ConductorRepository conductorRepository;

    /**
     * Obtiene la pagina de conductores activos, con filtro opcional por texto de busqueda.
     * @param search texto opcional para filtrar por nombres, cedula o licencia, nulo o vacio para traer todo
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con los conductores activos encontrados
     */
    public Page<ConductorResponse> listar(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return conductorRepository.findByActivoTrue(pageable).map(this::mapearAResponse);
        }
        return conductorRepository.buscarActivos(search.trim().toLowerCase(), pageable)
                .map(this::mapearAResponse);
    }

    /**
     * Recupera el detalle de un conductor activo existente.
     * @param id identificador del conductor que se desea consultar
     * @return datos del conductor encontrado
     * @throws IllegalArgumentException cuando no existe un conductor activo con ese identificador
     */
    public ConductorResponse buscarPorId(Long id) {
        Conductor conductor = obtenerConductorActivo(id);
        return mapearAResponse(conductor);
    }

    /**
     * Registra un nuevo conductor despues de validar que cedula y licencia no se repitan.
     * @param request datos personales, de licencia y de contacto del conductor por registrar
     * @return datos del conductor recien guardado
     * @throws IllegalArgumentException cuando la cedula o la licencia ya estan registradas, o el estado no es valido
     */
    @CacheEvict(value = "conductores", allEntries = true)
    public ConductorResponse crear(ConductorRequest request) {
        validarCedulaDuplicada(request.cedula());
        validarLicenciaDuplicada(request.numeroLicencia());

        Conductor conductor = Conductor.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .cedula(request.cedula())
                .numeroLicencia(request.numeroLicencia())
                .tipoLicencia(request.tipoLicencia())
                .fechaVencimientoLicencia(request.fechaVencimientoLicencia())
                .telefono(request.telefono())
                .email(request.email())
                .estado(convertirEstado(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        Conductor conductorGuardado = conductorRepository.save(conductor);
        return mapearAResponse(conductorGuardado);
    }

    /**
     * Reemplaza los datos de un conductor activo por los valores recibidos.
     * @param id identificador del conductor que se desea modificar
     * @param request nuevos datos personales, de licencia y de contacto para el conductor
     * @return datos del conductor ya actualizado
     * @throws IllegalArgumentException cuando el conductor no existe, la cedula o licencia chocan con otro registro, o el estado no es valido
     */
    @CacheEvict(value = "conductores", allEntries = true)
    public ConductorResponse actualizar(Long id, ConductorRequest request) {
        Conductor conductor = obtenerConductorActivo(id);

        if (!conductor.getCedula().equals(request.cedula())
                && conductorRepository.existsByCedula(request.cedula())) {
            throw new IllegalArgumentException("Ya existe un conductor con esa cedula");
        }

        if (!conductor.getNumeroLicencia().equals(request.numeroLicencia())
                && conductorRepository.existsByNumeroLicencia(request.numeroLicencia())) {
            throw new IllegalArgumentException("Ya existe un conductor con ese numero de licencia");
        }

        conductor.setNombres(request.nombres());
        conductor.setApellidos(request.apellidos());
        conductor.setCedula(request.cedula());
        conductor.setNumeroLicencia(request.numeroLicencia());
        conductor.setTipoLicencia(request.tipoLicencia());
        conductor.setFechaVencimientoLicencia(request.fechaVencimientoLicencia());
        conductor.setTelefono(request.telefono());
        conductor.setEmail(request.email());
        conductor.setEstado(convertirEstado(request.estado()));
        conductor.setActualizadoEn(Instant.now());

        Conductor conductorActualizado = conductorRepository.save(conductor);
        return mapearAResponse(conductorActualizado);
    }

    /**
     * Marca un conductor como inactivo y lo deja en estado inactivo.
     * @param id identificador del conductor que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe un conductor activo con ese identificador
     */
    @CacheEvict(value = "conductores", allEntries = true)
    public void desactivar(Long id) {
        Conductor conductor = obtenerConductorActivo(id);
        conductor.setActivo(false);
        conductor.setEstado(EstadoConductor.INACTIVO);
        conductor.setActualizadoEn(Instant.now());
        conductorRepository.save(conductor);
    }

    private Conductor obtenerConductorActivo(Long id) {
        return conductorRepository.findById(id)
                .filter(Conductor::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado"));
    }

    private void validarCedulaDuplicada(String cedula) {
        if (conductorRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException("Ya existe un conductor con esa cedula");
        }
    }

    private void validarLicenciaDuplicada(String numeroLicencia) {
        if (conductorRepository.existsByNumeroLicencia(numeroLicencia)) {
            throw new IllegalArgumentException("Ya existe un conductor con ese numero de licencia");
        }
    }

    private EstadoConductor convertirEstado(String estado) {
        try {
            return EstadoConductor.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de conductor no valido");
        }
    }

    private Boolean licenciaPorVencer(LocalDate fechaVencimiento) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        return !fechaVencimiento.isBefore(hoy) && !fechaVencimiento.isAfter(limite);
    }

    private ConductorResponse mapearAResponse(Conductor conductor) {
        return new ConductorResponse(
                conductor.getId(),
                conductor.getNombres(),
                conductor.getApellidos(),
                conductor.getCedula(),
                conductor.getNumeroLicencia(),
                conductor.getTipoLicencia(),
                conductor.getFechaVencimientoLicencia(),
                conductor.getTelefono(),
                conductor.getEmail(),
                conductor.getEstado().name(),
                conductor.getActivo(),
                licenciaPorVencer(conductor.getFechaVencimientoLicencia()),
                conductor.getCreadoEn(),
                conductor.getActualizadoEn()
        );
    }
}
