package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.Ciudad;
import ec.edu.uteq.sgroas.abd.entity.Provincia;
import ec.edu.uteq.sgroas.abd.entity.RolAbd;
import ec.edu.uteq.sgroas.abd.entity.Terminal;
import ec.edu.uteq.sgroas.abd.repository.CiudadRepository;
import ec.edu.uteq.sgroas.abd.repository.ProvinciaRepository;
import ec.edu.uteq.sgroas.abd.repository.RolAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoAbdService {

    private final ProvinciaRepository provinciaRepository;
    private final CiudadRepository ciudadRepository;
    private final TerminalRepository terminalRepository;
    private final RolAbdRepository rolAbdRepository;

    public AbdDtos.CatalogosResponse obtenerCatalogos() {
        List<AbdDtos.ProvinciaResponse> provincias = provinciaRepository.findAll().stream()
                .map(p -> new AbdDtos.ProvinciaResponse(p.getIdProvincia(), p.getNombre()))
                .toList();
        List<AbdDtos.CiudadResponse> ciudades = ciudadRepository.findAll().stream()
                .map(this::aCiudadResponse)
                .toList();
        List<AbdDtos.TerminalResponse> terminales = terminalRepository.findAll().stream()
                .map(this::aTerminalResponse)
                .toList();
        List<AbdDtos.RolResponse> roles = rolAbdRepository.findAll().stream()
                .map(r -> new AbdDtos.RolResponse(r.getIdRol(), r.getNombre(), r.getDescripcion()))
                .toList();
        return new AbdDtos.CatalogosResponse(provincias, ciudades, terminales, roles);
    }

    public Terminal buscarTerminal(Integer idTerminal) {
        return terminalRepository.findById(idTerminal)
                .orElseThrow(() -> new IllegalArgumentException("Terminal no encontrado: " + idTerminal));
    }

    private AbdDtos.CiudadResponse aCiudadResponse(Ciudad c) {
        Provincia p = c.getProvincia();
        return new AbdDtos.CiudadResponse(c.getIdCiudad(), c.getNombre(), p.getIdProvincia(), p.getNombre());
    }

    private AbdDtos.TerminalResponse aTerminalResponse(Terminal t) {
        Ciudad c = t.getCiudad();
        return new AbdDtos.TerminalResponse(t.getIdTerminal(), t.getNombre(), c.getIdCiudad(), c.getNombre());
    }
}
