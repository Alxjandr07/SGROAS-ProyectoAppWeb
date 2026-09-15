package ec.edu.uteq.sgroas.service;

import ec.edu.uteq.sgroas.dto.IncidentRequest;
import ec.edu.uteq.sgroas.dto.IncidentResponse;
import ec.edu.uteq.sgroas.entity.*;
import ec.edu.uteq.sgroas.repository.RouteAssignmentRepository;
import ec.edu.uteq.sgroas.repository.IncidentRepository;
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
public class IncidentService {

    private final IncidentRepository incidenteRepository;
    private final RouteAssignmentRepository asignacionRutaRepository;

    public Page<IncidentResponse> list(Pageable pageable) {
        List<IncidentResponse> contenido = listCached(pageable);
        return new PageImpl<>(contenido, pageable, contenido.size());
    }

    @Cacheable(value = "incidentes", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<IncidentResponse> listCached(Pageable pageable) {
        return incidenteRepository.findByActiveTrue(pageable)
                .map(this::mapearAResponse)
                .getContent();
    }

    public IncidentResponse findById(Long id) {
        Incident incidente = getActiveIncident(id);
        return mapearAResponse(incidente);
    }

    @CacheEvict(value = "incidentes", allEntries = true)
    public IncidentResponse create(IncidentRequest request) {
        RouteAssignment asignacion = asignacionRutaRepository.findById(request.assignmentId())
                .filter(RouteAssignment::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        Incident incidente = Incident.builder()
                .assignment(asignacion)
                .reportedBy(request.reportedBy())
                .type(toType(request.type()))
                .description(request.description())
                .incidentDate(request.incidentDate())
                .location(request.location())
                .severity(toSeverity(request.severity()))
                .status(toStatus(request.status()))
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Incident incidenteGuardado = incidenteRepository.save(incidente);
        return mapearAResponse(incidenteGuardado);
    }

    @CacheEvict(value = "incidentes", allEntries = true)
    public IncidentResponse update(Long id, IncidentRequest request) {
        Incident incidente = getActiveIncident(id);

        RouteAssignment asignacion = asignacionRutaRepository.findById(request.assignmentId())
                .filter(RouteAssignment::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada"));

        incidente.setAssignment(asignacion);
        incidente.setReportedBy(request.reportedBy());
        incidente.setType(toType(request.type()));
        incidente.setDescription(request.description());
        incidente.setIncidentDate(request.incidentDate());
        incidente.setLocation(request.location());
        incidente.setSeverity(toSeverity(request.severity()));
        incidente.setStatus(toStatus(request.status()));
        incidente.setUpdatedAt(Instant.now());

        Incident incidenteActualizado = incidenteRepository.save(incidente);
        return mapearAResponse(incidenteActualizado);
    }

    @CacheEvict(value = "incidentes", allEntries = true)
    public void desactivar(Long id) {
        Incident incidente = getActiveIncident(id);
        incidente.setActive(false);
        incidente.setStatus(IncidentStatus.CERRADO);
        incidente.setUpdatedAt(Instant.now());
        incidenteRepository.save(incidente);
    }

    private Incident getActiveIncident(Long id) {
        return incidenteRepository.findById(id)
                .filter(Incident::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Incident no encontrado"));
    }

    private IncidentType toType(String type) {
        try {
            return IncidentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de incidente no valido");
        }
    }

    private IncidentSeverity toSeverity(String severity) {
        try {
            return IncidentSeverity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gravedad de incidente no valida");
        }
    }

    private IncidentStatus toStatus(String status) {
        try {
            return IncidentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de incidente no valido");
        }
    }

    private IncidentResponse mapearAResponse(Incident incidente) {
        return new IncidentResponse(
                incidente.getId(),
                incidente.getAssignment().getId(),
                incidente.getReportedBy(),
                incidente.getType().name(),
                incidente.getDescription(),
                incidente.getIncidentDate(),
                incidente.getLocation(),
                incidente.getSeverity().name(),
                incidente.getStatus().name(),
                incidente.getActive(),
                incidente.getCreatedAt(),
                incidente.getUpdatedAt()
        );
    }
}
