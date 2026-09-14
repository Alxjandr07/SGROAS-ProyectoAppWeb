package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.AbdUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abd/unidades")
@RequiredArgsConstructor
public class AbdUnitController {

    private final AbdUnitService unidadAbdService;

    /**
     * Atiende la peticion de consulta paginada de unidades con filtros opcionales.
     * @param estado estado por el que se desea filtrar las unidades, puede ser nulo para no filtrar.
     * @param search texto para buscar coincidencias en placa, disco o modelo, puede ser nulo para no filtrar.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de unidades encontradas y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDtos.UnitResponse>> list(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "idUnidad") Pageable pageable) {
        return ResponseEntity.ok(unidadAbdService.list(estado, search, pageable));
    }

    /**
     * Atiende la peticion de consulta del detalle de una unidad existente.
     * @param id identificador de la unidad recibido en la ruta de la peticion.
     * @return respuesta con los datos de la unidad encontrada y estado 200.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AbdDtos.UnitResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(unidadAbdService.findById(id));
    }

    /**
     * Atiende la peticion de registro de una unidad nueva en el sistema.
     * @param request cuerpo de la peticion con los datos validados de la unidad por registrar.
     * @return respuesta con los datos de la unidad guardada y estado 201.
     */
    @PostMapping
    public ResponseEntity<AbdDtos.UnitResponse> create(@Valid @RequestBody AbdDtos.UnitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadAbdService.create(request));
    }

    /**
     * Atiende la peticion de modificacion de los datos de una unidad existente.
     * @param id identificador de la unidad recibido en la ruta de la peticion.
     * @param request cuerpo de la peticion con los nuevos datos validados de la unidad.
     * @return respuesta con los datos de la unidad actualizada y estado 200.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.UnitResponse> update(@PathVariable Integer id,
                                                             @Valid @RequestBody AbdDtos.UnitRequest request) {
        return ResponseEntity.ok(unidadAbdService.update(id, request));
    }

    /**
     * Atiende la peticion de supresion del registro de una unidad existente.
     * @param id identificador de la unidad recibido en la ruta de la peticion.
     * @return respuesta sin contenido y estado 204 cuando la supresion termina con exito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        unidadAbdService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
