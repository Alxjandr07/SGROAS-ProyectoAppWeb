package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.ProgramacionAbdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abd/programaciones")
@RequiredArgsConstructor
public class ProgramacionAbdController {

    private final ProgramacionAbdService programacionAbdService;

    /**
     * Atiende la peticion de consulta paginada de programaciones de viajes con filtros opcionales.
     * @param estado estado por el que se desea filtrar las programaciones, puede ser nulo para no filtrar.
     * @param idConductor identificador del conductor por el que se desea filtrar, puede ser nulo para no filtrar.
     * @param idRuta identificador de la ruta por la que se desea filtrar, puede ser nulo para no filtrar.
     * @param fechaDesde fecha inicial del rango por el que se desea filtrar, puede ser nula para no limitar.
     * @param fechaHasta fecha final del rango por el que se desea filtrar, puede ser nula para no limitar.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de programaciones encontradas y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDtos.ProgramacionResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idConductor,
            @RequestParam(required = false) Integer idRuta,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaDesde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaHasta,
            @PageableDefault(size = 50, sort = "idProgramacion") Pageable pageable) {
        return ResponseEntity.ok(programacionAbdService.listar(estado, idConductor, idRuta, fechaDesde, fechaHasta, pageable));
    }

    /**
     * Atiende la peticion de registro de la programacion de un viaje nuevo.
     * @param request cuerpo de la peticion con los datos validados del viaje por programar.
     * @return respuesta con los datos de la programacion guardada y estado 201.
     */
    @PostMapping
    public ResponseEntity<AbdDtos.ProgramacionResponse> crear(@Valid @RequestBody AbdDtos.ProgramacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programacionAbdService.crear(request));
    }

    /**
     * Atiende la peticion de modificacion de los datos de una programacion existente.
     * @param id identificador de la programacion recibido en la ruta de la peticion.
     * @param request cuerpo de la peticion con los nuevos datos validados de la programacion.
     * @return respuesta con los datos de la programacion actualizada y estado 200.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.ProgramacionResponse> actualizar(@PathVariable Integer id,
                                                                   @Valid @RequestBody AbdDtos.ProgramacionRequest request) {
        return ResponseEntity.ok(programacionAbdService.actualizar(id, request));
    }

    /**
     * Atiende la peticion de supresion del registro de una programacion existente.
     * @param id identificador de la programacion recibido en la ruta de la peticion.
     * @return respuesta sin contenido y estado 204 cuando la supresion termina con exito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        programacionAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
