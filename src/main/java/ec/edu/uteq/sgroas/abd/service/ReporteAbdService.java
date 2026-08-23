package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.repository.AlertaRepository;
import ec.edu.uteq.sgroas.abd.repository.IncidenteAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.ProgramacionRepository;
import ec.edu.uteq.sgroas.abd.repository.RutaAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteAbdService {

    private final ProgramacionRepository programacionRepository;
    private final IncidenteAbdRepository incidenteRepository;
    private final AlertaRepository alertaRepository;
    private final UnidadRepository unidadRepository;
    private final RutaAbdRepository rutaAbdRepository;

    public AbdDtos.ResumenResponse resumen() {
        long totalProgramaciones = programacionRepository.count();
        long programacionesActivas = programacionRepository.findByEstadoIgnoreCase("Programado",
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        long totalIncidentes = incidenteRepository.count();
        long incidentesAlto = incidenteRepository.findByNivelSugeridoIgnoreCase("ALTO",
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        long totalAlertas = alertaRepository.count();
        long totalUnidades = unidadRepository.count();
        long unidadesMantenimiento = unidadRepository.findByEstadoIgnoreCase("En Mantenimiento",
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        long totalRutas = rutaAbdRepository.count();

        return new AbdDtos.ResumenResponse(
                totalProgramaciones, programacionesActivas,
                totalIncidentes, incidentesAlto,
                totalAlertas, totalUnidades, unidadesMantenimiento,
                totalRutas
        );
    }

    public java.util.List<AbdDtos.ConteoResponse> incidentesPorNivel() {
        return incidenteRepository.contarPorNivel().stream()
                .map(f -> new AbdDtos.ConteoResponse(f.getNivel(), f.getTotal()))
                .toList();
    }

    public java.util.List<AbdDtos.ConteoResponse> incidentesPorEstado() {
        return incidenteRepository.contarPorEstado().stream()
                .map(f -> new AbdDtos.ConteoResponse(f.getEstado(), f.getTotal()))
                .toList();
    }

    public java.util.List<AbdDtos.ConteoResponse> unidadesPorEstado() {
        return unidadRepository.contarPorEstado().stream()
                .map(f -> new AbdDtos.ConteoResponse(f.getClave(), f.getTotal()))
                .toList();
    }

    public java.util.List<AbdDtos.ConteoResponse> programacionesPorEstado() {
        return programacionRepository.contarPorEstado().stream()
                .map(f -> new AbdDtos.ConteoResponse(f.getClave(), f.getTotal()))
                .toList();
    }

    public java.util.List<AbdDtos.ConteoResponse> programacionesPorMes() {
        return programacionRepository.contarPorMes().stream()
                .map(f -> new AbdDtos.ConteoResponse(f.getClave(), f.getTotal()))
                .toList();
    }

    public java.util.List<AbdDtos.TopRutaResponse> topRutas() {
        return rutaAbdRepository.topRutas().stream()
                .map(f -> new AbdDtos.TopRutaResponse(f.getId(), f.getDescripcion(), f.getTotal()))
                .toList();
    }
}
