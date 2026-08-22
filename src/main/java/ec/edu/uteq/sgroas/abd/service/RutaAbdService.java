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

    @Transactional(readOnly = true)
    public Page<AbdDtos.RutaAbdResponse> listar(Pageable pageable) {
        return rutaAbdRepository.findAll(pageable).map(this::aResponse);
    }

    @Transactional(readOnly = true)
    public AbdDtos.RutaAbdResponse buscarPorId(Integer idRuta) {
        return rutaAbdRepository.findById(idRuta).map(this::aResponse)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + idRuta));
    }

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
