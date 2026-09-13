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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramacionAbdService {

    private final ProgramacionRepository programacionRepository;
    private final RutaAbdRepository rutaAbdRepository;
    private final UnidadRepository unidadRepository;
    private final ConductorAbdRepository conductorAbdRepository;

    /**
     * Recupera las programaciones de viajes aplicando filtros opcionales de forma paginada.
     * @param estado estado por el que se desea filtrar las programaciones, puede ser nulo para no filtrar.
     * @param idConductor identificador del conductor por el que se desea filtrar, puede ser nulo para no filtrar.
     * @param idRuta identificador de la ruta por la que se desea filtrar, puede ser nulo para no filtrar.
     * @param fechaDesde fecha inicial del rango por el que se desea filtrar, puede ser nula para no limitar.
     * @param fechaHasta fecha final del rango por el que se desea filtrar, puede ser nula para no limitar.
     * @param pageable configuracion de paginacion y orden solicitada por el cliente.
     * @return pagina con los datos resumidos de las programaciones encontradas.
     */
    @Transactional(readOnly = true)
    public Page<AbdDtos.ProgramacionResponse> listar(String estado, Integer idConductor, Integer idRuta,
                                                     LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        String estadoFiltro = (estado == null || estado.isBlank()) ? null : estado.trim().toLowerCase();
        Integer conductorFiltro = (idConductor != null && idConductor > 0) ? idConductor : null;
        Integer rutaFiltro = (idRuta != null && idRuta > 0) ? idRuta : null;
        if (estadoFiltro == null && conductorFiltro == null && rutaFiltro == null
                && fechaDesde == null && fechaHasta == null) {
            return programacionRepository.findAll(pageable).map(this::aResponse);
        }
        return programacionRepository.buscarConFiltros(estadoFiltro, conductorFiltro, rutaFiltro,
                fechaDesde, fechaHasta, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                .map(this::aResponse);
    }

    /**
     * Registra la programacion de un viaje validando horarios y disponibilidad de la unidad asignada.
     * @param request datos de fecha, horarios, ruta, unidad y conductor del viaje que se desea programar.
     * @return datos resumidos de la programacion guardada.
     * @throws IllegalArgumentException cuando los horarios son invalidos o la ruta, la unidad o el conductor no existen.
     * @throws IllegalStateException cuando la unidad asignada no se encuentra en estado activo.
     */
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

    /**
     * Modifica los datos de una programacion de viaje existente con sus nuevas asignaciones.
     * @param idProgramacion identificador de la programacion que se desea modificar.
     * @param request nuevos datos de fecha, horarios, ruta, unidad y conductor que reemplazaran a los actuales.
     * @return datos resumidos de la programacion actualizada.
     * @throws IllegalArgumentException cuando la programacion no existe, los horarios son invalidos o alguna referencia no existe.
     * @throws IllegalStateException cuando la unidad asignada no se encuentra en estado activo.
     */
    public AbdDtos.ProgramacionResponse actualizar(Integer idProgramacion, AbdDtos.ProgramacionRequest request) {
        Programacion programacion = programacionRepository.findById(idProgramacion)
                .orElseThrow(() -> new IllegalArgumentException("Programacion no encontrada: " + idProgramacion));
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
        programacion.setFecha(request.fecha());
        programacion.setHoraSalida(request.horaSalida());
        programacion.setHoraEstimadaLlegada(request.horaEstimadaLlegada());
        if (request.estado() != null) {
            programacion.setEstado(request.estado());
        }
        programacion.setRuta(ruta);
        programacion.setUnidad(unidad);
        programacion.setConductor(conductor);
        return aResponse(programacionRepository.save(programacion));
    }

    /**
     * Suprime del sistema el registro de una programacion de viaje existente.
     * @param idProgramacion identificador de la programacion que se desea suprimir.
     * @throws IllegalArgumentException cuando no existe una programacion con el identificador indicado.
     */
    public void eliminar(Integer idProgramacion) {
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
