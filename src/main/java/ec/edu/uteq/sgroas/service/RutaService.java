package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.RutaRequest;
import ec.edu.uteq.sgroas.dto.RutaResponse;
import ec.edu.uteq.sgroas.entity.EstadoRuta;
import ec.edu.uteq.sgroas.entity.Ruta;
import ec.edu.uteq.sgroas.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;

    /**
     * Obtiene la pagina de rutas activas convertidas a formato de respuesta.
     * @param pageable objeto con numero de pagina, tamanio y orden solicitados para la consulta
     * @return pagina con las rutas activas encontradas
     */
    public Page<RutaResponse> listar(Pageable pageable) {
        List<RutaResponse> contenido = listarCacheable(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    /**
     * Obtiene desde la memoria cache la lista de rutas activas de la pagina solicitada.
     * @param pageable objeto con numero de pagina y tamanio que identifican la entrada guardada en cache
     * @return lista de rutas activas correspondientes a la pagina pedida
     */
    @Cacheable(value = "rutas", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<RutaResponse> listarCacheable(Pageable pageable) {
        return rutaRepository.findByActivoTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    /**
     * Recupera el detalle de una ruta activa existente.
     * @param id identificador de la ruta que se desea consultar
     * @return datos de la ruta encontrada
     * @throws IllegalArgumentException cuando no existe una ruta activa con ese identificador
     */
    public RutaResponse buscarPorId(Long id) {
        Ruta ruta = obtenerRutaActiva(id);
        return mapearAResponse(ruta);
    }

    /**
     * Registra una nueva ruta despues de validar que su codigo no se repita.
     * @param request datos de la ruta con codigo, nombre, origen, destino, distancia, duracion y estado
     * @return datos de la ruta recien guardada
     * @throws IllegalArgumentException cuando ya existe otra ruta con el mismo codigo o el estado no es valido
     */
    @CacheEvict(value = "rutas", allEntries = true)
    public RutaResponse crear(RutaRequest request) {
        if (rutaRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Ya existe una ruta con ese codigo");
        }

        Ruta ruta = Ruta.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .origen(request.origen())
                .destino(request.destino())
                .distanciaKm(request.distanciaKm())
                .duracionEstimadaMin(request.duracionEstimadaMin())
                .estado(convertirEstado(request.estado()))
                .activo(true)
                .creadoEn(Instant.now())
                .actualizadoEn(Instant.now())
                .build();

        Ruta rutaGuardada = rutaRepository.save(ruta);
        return mapearAResponse(rutaGuardada);
    }

    /**
     * Reemplaza los datos de una ruta activa por los valores recibidos.
     * @param id identificador de la ruta que se desea modificar
     * @param request nuevos datos de la ruta con codigo, nombre, origen, destino, distancia, duracion y estado
     * @return datos de la ruta ya actualizada
     * @throws IllegalArgumentException cuando la ruta no existe, el codigo choca con otra ruta o el estado no es valido
     */
    @CacheEvict(value = "rutas", allEntries = true)
    public RutaResponse actualizar(Long id, RutaRequest request) {
        Ruta ruta = obtenerRutaActiva(id);

        if (!ruta.getCodigo().equals(request.codigo())
                && rutaRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Ya existe una ruta con ese codigo");
        }

        ruta.setCodigo(request.codigo());
        ruta.setNombre(request.nombre());
        ruta.setOrigen(request.origen());
        ruta.setDestino(request.destino());
        ruta.setDistanciaKm(request.distanciaKm());
        ruta.setDuracionEstimadaMin(request.duracionEstimadaMin());
        ruta.setEstado(convertirEstado(request.estado()));
        ruta.setActualizadoEn(Instant.now());

        Ruta rutaActualizada = rutaRepository.save(ruta);
        return mapearAResponse(rutaActualizada);
    }

    /**
     * Marca una ruta como inactiva y la deja en estado inactiva.
     * @param id identificador de la ruta que se desea dar de baja
     * @throws IllegalArgumentException cuando no existe una ruta activa con ese identificador
     */
    @CacheEvict(value = "rutas", allEntries = true)
    public void desactivar(Long id) {
        Ruta ruta = obtenerRutaActiva(id);
        ruta.setActivo(false);
        ruta.setEstado(EstadoRuta.INACTIVA);
        ruta.setActualizadoEn(Instant.now());
        rutaRepository.save(ruta);
    }

    private Ruta obtenerRutaActiva(Long id) {
        return rutaRepository.findById(id)
                .filter(Ruta::getActivo)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));
    }

    private EstadoRuta convertirEstado(String estado) {
        try {
            return EstadoRuta.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de ruta no valido");
        }
    }

    private RutaResponse mapearAResponse(Ruta ruta) {
        return new RutaResponse(
                ruta.getId(),
                ruta.getCodigo(),
                ruta.getNombre(),
                ruta.getOrigen(),
                ruta.getDestino(),
                ruta.getDistanciaKm(),
                ruta.getDuracionEstimadaMin(),
                ruta.getEstado().name(),
                ruta.getActivo(),
                ruta.getCreadoEn(),
                ruta.getActualizadoEn()
        );
    }
}
