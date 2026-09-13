package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.ConductorRequest;
import ec.edu.uteq.sgroas.dto.ConductorResponse;
import ec.edu.uteq.sgroas.service.ConductorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conductores")
@RequiredArgsConstructor
public class ConductorController {

    private final ConductorService conductorService;

    /**
     * Recupera la lista paginada de conductores con filtro opcional por texto.
     * @param search criterio opcional para filtrar por nombre, cédula u otros datos.
     * @param pageable configuración de paginación y ordenamiento solicitada por el cliente.
     * @return respuesta HTTP con la página de conductores encontrados.
     */
    @GetMapping
    public ResponseEntity<Page<ConductorResponse>> listar(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(conductorService.listar(search, pageable));
    }

    /**
     * Obtiene el detalle de un conductor a partir de su identificador.
     * @param id identificador único del conductor que se desea consultar.
     * @return respuesta HTTP con los datos del conductor encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConductorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conductorService.buscarPorId(id));
    }

    /**
     * Registra un nuevo conductor con los datos recibidos en la solicitud.
     * @param request objeto con los datos personales y de licencia del conductor.
     * @return respuesta HTTP con estado creado y los datos del conductor registrado.
     */
    @PostMapping
    public ResponseEntity<ConductorResponse> crear(
            @Valid @RequestBody ConductorRequest request
    ) {
        ConductorResponse response = conductorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Modifica los datos de un conductor ya existente.
     * @param id identificador único del conductor que se desea modificar.
     * @param request objeto con los nuevos valores para actualizar el conductor.
     * @return respuesta HTTP con los datos actualizados del conductor.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConductorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConductorRequest request
    ) {
        return ResponseEntity.ok(conductorService.actualizar(id, request));
    }

    /**
     * Desactiva lógicamente un conductor para que deje de estar disponible.
     * @param id identificador único del conductor que se desea desactivar.
     * @return respuesta HTTP sin contenido que confirma la operación realizada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        conductorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}