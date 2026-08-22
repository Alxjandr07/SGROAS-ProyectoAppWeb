package ec.edu.uteq.sgroas.abd.service;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.ConductorAbd;
import ec.edu.uteq.sgroas.abd.entity.Programacion;
import ec.edu.uteq.sgroas.abd.entity.RutaAbd;
import ec.edu.uteq.sgroas.abd.entity.Unidad;
import ec.edu.uteq.sgroas.abd.repository.ConductorAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.ProgramacionRepository;
import ec.edu.uteq.sgroas.abd.repository.RutaAbdRepository;
import ec.edu.uteq.sgroas.abd.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramacionAbdService {

    private final ProgramacionRepository programacionRepository;
    private final RutaAbdRepository rutaAbdRepository;
    private final UnidadRepository unidadRepository;
    private final ConductorAbdRepository conductorAbdRepository;

    @Transactional(readOnly = true)
    public Page<AbdDtos.ProgramacionResponse> listar(String estado, Long idConductor, Long idRuta, Pageable pageable) {
        String estadoFiltro = (estado == null || estado.isBlank()) ? null : estado;
        Long conductorFiltro = idConductor != null && idConductor > 0 ? idConductor : null;
        Long rutaFiltro = idRuta != null && idRuta > 0 ? idRuta : null;
        if (estadoFiltro == null && conductorFiltro == null && rutaFiltro == null) {
            return programacionRepository.findAll(pageable).map(this::aResponse);
        }
        return programacionRepository.buscarConFiltros(estadoFiltro, conductorFiltro, rutaFiltro, pageable).map(this::aResponse);
    }

    public AbdDtos.ProgramacionResponse crear(AbdDtos.ProgramacionRequest request) {
        validarHoras(request.horaSalida(), request.horaEstimadaLlegada());
        RutaAbd ruta = rutaAbdRepository.findById(request.idRuta())
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + request.idRuta()));
        Unidad unidad = unidadRepository.findById(request.idUnidad())
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada: " + request.idUnidad()));
        ConductorAbd conductor = conductorAbdRepository.findById(request.idConductor())
                .orElseThrow(() -> new IllegalArgumentException("Conductor no encontrado: " + request.idConductor()));
        if (!"Activo".equalsIgnoreCase(unidad.getEstado())) {
            throw new IllegalStateException("La unidad " + unidad.getPlaca() + " no esta activa");
        }
        Programacion programacion = Programacion.builder()
                .fecha(request.fecha())
                .horaSalida(request.horaSalida())
                .horaEstimadaLlegada(request.horaEstimadaLlegada())
                .estado(request.estado() == null ? "Programado" : request.estado())
                .ruta(ruta)
                .unidad(unidad)
                .conductor(conductor)
                .build();
        return aResponse(programacionRepository.save(programacion));
    }

    public void eliminar(Long idProgramacion) {
        if (!programacionRepository.existsById(idProgramacion)) {
            throw new IllegalArgumentException("Programacion no encontrada: " + idProgramacion);
        }
        programacionRepository.deleteById(idProgramacion);
    }

    private void validarHoras(java.time.LocalTime salida, java.time.LocalTime llegada) {
        if (!llegada.isAfter(salida)) {
            throw new IllegalArgumentException("La hora de llegada debe ser posterior a la hora de salida");
        }
    }

    private AbdDtos.ProgramacionResponse aResponse(Programacion p) {
        return new AbdDtos.ProgramacionResponse(
                p.getIdProgramacion(), p.getFecha(), p.getHoraSalida(), p.getHoraEstimadaLlegada(), p.getEstado(),
                p.getRuta().getIdRuta(), p.getRuta().getTerminalOrigen().getNombre() + " -> " + p.getRuta().getTerminalDestino().getNombre(),
                p.getUnidad().getIdUnidad(), p.getUnidad().getPlaca(),
                p.getConductor().getIdConductor(), p.getConductor().getNombres()
        );
    }
}
