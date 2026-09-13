package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.IncidenteAbdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abd/incidentes")
@RequiredArgsConstructor
public class IncidenteAbdController {

    private final IncidenteAbdService incidenteAbdService;

    /**
     * Atiende la peticion de consulta paginada de incidentes con filtros opcionales.
     * @param estado estado por el que se desea filtrar los incidentes, puede ser nulo para no filtrar.
     * @param nivel nivel de riesgo sugerido por el que se desea filtrar, puede ser nulo para no filtrar.
     * @param search texto para buscar coincidencias en los datos del incidente, puede ser nulo para no filtrar.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de incidentes encontrados y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDtos.IncidenteAbdResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "idIncidente") Pageable pageable) {
        return ResponseEntity.ok(incidenteAbdService.listar(estado, nivel, search, pageable));
    }

    /**
     * Atiende la peticion de registro de un incidente nuevo en el sistema.
     * @param request cuerpo de la peticion con los datos validados del incidente por reportar.
     * @return respuesta con los datos del incidente guardado y estado 201.
     */
    @PostMapping
    public ResponseEntity<AbdDtos.IncidenteAbdResponse> crear(@Valid @RequestBody AbdDtos.IncidenteAbdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenteAbdService.crear(request));
    }

    /**
     * Atiende la peticion de modificacion de los datos de un incidente existente.
     * @param id identificador del incidente recibido en la ruta de la peticion.
     * @param request cuerpo de la peticion con los nuevos datos validados del incidente.
     * @return respuesta con los datos del incidente actualizado y estado 200.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.IncidenteAbdResponse> actualizar(@PathVariable Integer id,
                                                                  @Valid @RequestBody AbdDtos.IncidenteAbdRequest request) {
        return ResponseEntity.ok(incidenteAbdService.actualizar(id, request));
    }

    /**
     * Atiende la peticion de supresion del registro de un incidente existente.
     * @param id identificador del incidente recibido en la ruta de la peticion.
     * @return respuesta sin contenido y estado 204 cuando la supresion termina con exito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        incidenteAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
