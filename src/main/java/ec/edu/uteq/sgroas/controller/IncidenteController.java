package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.IncidenteRequest;
import ec.edu.uteq.sgroas.dto.IncidenteResponse;
import ec.edu.uteq.sgroas.service.IncidenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidentes")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;

    /**
     * Recupera la lista paginada de incidentes registrados en el sistema.
     * @param pageable configuración de paginación y ordenamiento solicitada por el cliente.
     * @return respuesta HTTP con la página de incidentes encontrados.
     */
    @GetMapping
    public ResponseEntity<Page<IncidenteResponse>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(incidenteService.listar(pageable));
    }

    /**
     * Obtiene el detalle de un incidente a partir de su identificador.
     * @param id identificador único del incidente que se desea consultar.
     * @return respuesta HTTP con los datos del incidente encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidenteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(incidenteService.buscarPorId(id));
    }

    /**
     * Registra un nuevo incidente con los datos recibidos en la solicitud.
     * @param request objeto con la descripción, gravedad y datos del incidente reportado.
     * @return respuesta HTTP con estado creado y los datos del incidente registrado.
     */
    @PostMapping
    public ResponseEntity<IncidenteResponse> crear(
            @Valid @RequestBody IncidenteRequest request
    ) {
        IncidenteResponse response = incidenteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Modifica los datos de un incidente ya existente.
     * @param id identificador único del incidente que se desea modificar.
     * @param request objeto con los nuevos valores para actualizar el incidente.
     * @return respuesta HTTP con los datos actualizados del incidente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IncidenteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody IncidenteRequest request
    ) {
        return ResponseEntity.ok(incidenteService.actualizar(id, request));
    }

    /**
     * Desactiva lógicamente un incidente para que deje de estar vigente.
     * @param id identificador único del incidente que se desea desactivar.
     * @return respuesta HTTP sin contenido que confirma la operación realizada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        incidenteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
