package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.RutaAbd;
import ec.edu.uteq.sgroas.abd.repository.ProgramacionRepository;
import ec.edu.uteq.sgroas.abd.repository.RutaAbdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RutaAbdService {

    private final RutaAbdRepository rutaAbdRepository;
    private final CatalogoAbdService catalogoAbdService;

    /**
     * Recupera las rutas registradas aplicando un filtro de busqueda opcional de forma paginada.
     * @param search texto para buscar coincidencias en terminales de origen o destino, puede ser nulo para traer todo.
     * @param pageable configuracion de paginacion y orden solicitada por el cliente.
     * @return pagina con los datos resumidos de las rutas encontradas.
     */
    @Transactional(readOnly = true)
    public Page<AbdDtos.RutaAbdResponse> listar(String search, Pageable pageable) {
        String filtro = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        Page<RutaAbd> page = filtro == null
                ? rutaAbdRepository.findAll(pageable)
                : rutaAbdRepository.buscar(filtro, pageable);
        return page.map(this::aResponse);
    }

    /**
     * Obtiene el detalle de una ruta existente a partir de su identificador.
     * @param idRuta identificador de la ruta que se desea consultar.
     * @return datos resumidos de la ruta encontrada.
     * @throws IllegalArgumentException cuando no existe una ruta con el identificador indicado.
     */
    @Transactional(readOnly = true)
    public AbdDtos.RutaAbdResponse buscarPorId(Integer idRuta) {
        return rutaAbdRepository.findById(idRuta).map(this::aResponse)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + idRuta));
    }

    /**
     * Registra una ruta nueva entre dos terminales distintos con su precio de pasaje.
     * @param request datos de los terminales de origen y destino junto con el precio del pasaje.
     * @return datos resumidos de la ruta guardada.
     * @throws IllegalArgumentException cuando el origen y el destino son el mismo terminal o un terminal no existe.
     */
    public AbdDtos.RutaAbdResponse crear(AbdDtos.RutaAbdRequest request) {
        if (request.idTerminalOrigen().equals(request.idTerminalDestino())) {
            throw new IllegalArgumentException("El origen y el destino no pueden ser el mismo terminal");
        }
        RutaAbd ruta = RutaAbd.builder()
                .terminalOrigen(catalogoAbdService.buscarTerminal(request.idTerminalOrigen()))
                .terminalDestino(catalogoAbdService.buscarTerminal(request.idTerminalDestino()))
                .precioPasaje(request.precioPasaje())
                .build();
        return aResponse(rutaAbdRepository.save(ruta));
    }

    /**
     * Modifica los terminales y el precio de una ruta existente.
     * @param idRuta identificador de la ruta que se desea modificar.
     * @param request nuevos datos de terminales y precio que reemplazaran a los actuales.
     * @return datos resumidos de la ruta actualizada.
     * @throws IllegalArgumentException cuando la ruta no existe, un terminal no existe o el origen y el destino coinciden.
     */
    public AbdDtos.RutaAbdResponse actualizar(Integer idRuta, AbdDtos.RutaAbdRequest request) {
        if (request.idTerminalOrigen().equals(request.idTerminalDestino())) {
            throw new IllegalArgumentException("El origen y el destino no pueden ser el mismo terminal");
        }
        RutaAbd ruta = rutaAbdRepository.findById(idRuta)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + idRuta));
        ruta.setTerminalOrigen(catalogoAbdService.buscarTerminal(request.idTerminalOrigen()));
        ruta.setTerminalDestino(catalogoAbdService.buscarTerminal(request.idTerminalDestino()));
        ruta.setPrecioPasaje(request.precioPasaje());
        return aResponse(rutaAbdRepository.save(ruta));
    }

    /**
     * Suprime del sistema el registro de una ruta existente.
     * @param idRuta identificador de la ruta que se desea suprimir.
     * @throws IllegalArgumentException cuando no existe una ruta con el identificador indicado.
     */
    public void eliminar(Integer idRuta) {
        if (!rutaAbdRepository.existsById(idRuta)) {
            throw new IllegalArgumentException("Ruta no encontrada: " + idRuta);
        }
        rutaAbdRepository.deleteById(idRuta);
    }

    private AbdDtos.RutaAbdResponse aResponse(RutaAbd r) {
        return new AbdDtos.RutaAbdResponse(
                r.getIdRuta(),
                r.getTerminalOrigen().getIdTerminal(),
                r.getTerminalOrigen().getNombre() + " (" + r.getTerminalOrigen().getCiudad().getNombre() + ")",
                r.getTerminalDestino().getIdTerminal(),
                r.getTerminalDestino().getNombre() + " (" + r.getTerminalDestino().getCiudad().getNombre() + ")",
                r.getPrecioPasaje(),
                rutaAbdRepository.contarProgramaciones(r.getIdRuta())
        );
    }
}
