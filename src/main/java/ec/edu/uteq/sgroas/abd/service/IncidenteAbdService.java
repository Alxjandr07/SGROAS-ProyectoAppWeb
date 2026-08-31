package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.Alerta;
import ec.edu.uteq.sgroas.abd.entity.IncidenteAbd;
import ec.edu.uteq.sgroas.abd.entity.Unidad;
import ec.edu.uteq.sgroas.abd.repository.AlertaRepository;
import ec.edu.uteq.sgroas.abd.repository.IncidenteAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidenteAbdService {

    private final IncidenteAbdRepository incidenteRepository;
    private final AlertaRepository alertaRepository;
    private final UnidadRepository unidadRepository;

    @Transactional(readOnly = true)
    public Page<AbdDtos.IncidenteAbdResponse> listar(String estado, String nivel, String search, Pageable pageable) {
        String estadoFiltro = (estado == null || estado.isBlank()) ? null : estado.trim().toLowerCase();
        String nivelFiltro = (nivel == null || nivel.isBlank()) ? null : nivel.trim().toLowerCase();
        String searchFiltro = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        if (estadoFiltro == null && nivelFiltro == null && searchFiltro == null) {
            return incidenteRepository.findAll(pageable).map(this::aResponse);
        }
        if (searchFiltro != null) {
            return incidenteRepository
                    .buscarConFiltros(estadoFiltro, nivelFiltro, searchFiltro,
                            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                    .map(this::aResponse);
        }
        Page<IncidenteAbd> page;
        if (estadoFiltro != null) {
            page = incidenteRepository.findByEstadoIgnoreCase(estadoFiltro, pageable);
        } else {
            page = incidenteRepository.findByNivelSugeridoIgnoreCase(nivelFiltro, pageable);
        }
        return page.map(this::aResponse);
    }

    public AbdDtos.IncidenteAbdResponse crear(AbdDtos.IncidenteAbdRequest request) {
        Unidad unidad = unidadRepository.findById(request.idUnidad())
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + request.idUnidad()));
        IncidenteAbd incidente = IncidenteAbd.builder()
                .tipo(request.tipo())
                .descripcion(request.descripcion())
                .nivelSugerido(request.nivelSugerido())
                .evidencia(request.evidencia())
                .estado(request.estado() == null ? "Reportado" : request.estado())
                .unidad(unidad)
                .build();
        incidente = incidenteRepository.save(incidente);

        if ("ALTO".equalsIgnoreCase(incidente.getNivelSugerido())) {
            generarAlerta(incidente);
        }
        return aResponse(incidente);
    }

    public AbdDtos.IncidenteAbdResponse actualizar(Integer idIncidente, AbdDtos.IncidenteAbdRequest request) {
        IncidenteAbd incidente = incidenteRepository.findById(idIncidente)
                .orElseThrow(() -> new IllegalArgumentException("Incidente no encontrado: " + idIncidente));
        Unidad unidad = unidadRepository.findById(request.idUnidad())
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + request.idUnidad()));
        incidente.setTipo(request.tipo());
        incidente.setDescripcion(request.descripcion());
        incidente.setNivelSugerido(request.nivelSugerido());
        if (request.evidencia() != null) {
            incidente.setEvidencia(request.evidencia());
        }
        if (request.estado() != null) {
            incidente.setEstado(request.estado());
        }
        incidente.setUnidad(unidad);
        return aResponse(incidenteRepository.save(incidente));
    }

    public void eliminar(Integer idIncidente) {
        if (!incidenteRepository.existsById(idIncidente)) {
            throw new IllegalArgumentException("Incidente no encontrado: " + idIncidente);
        }
        incidenteRepository.deleteById(idIncidente);
    }

    private void generarAlerta(IncidenteAbd incidente) {
        Alerta alerta = Alerta.builder()
                .nivelRiesgo("ALTO")
                .descripcion(incidente.getDescripcion())
                .incidente(incidente)
                .build();
        alertaRepository.save(alerta);
    }

    private AbdDtos.IncidenteAbdResponse aResponse(IncidenteAbd i) {
        return new AbdDtos.IncidenteAbdResponse(
                i.getIdIncidente(), i.getTipo(), i.getDescripcion(),
                i.getNivelSugerido(), i.getFechaIncidente().toString(), i.getEvidencia(),
                i.getEstado(), i.getUnidad().getIdUnidad(), i.getUnidad().getPlaca()
        );
    }
}
