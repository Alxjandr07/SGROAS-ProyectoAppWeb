package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.AbdRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abd/rutas")
@RequiredArgsConstructor
public class AbdRouteController {

    private final AbdRouteService rutaAbdService;

    /**
     * Atiende la peticion de consulta paginada de rutas con busqueda opcional.
     * @param search texto para buscar coincidencias en terminales de origen o destino, puede ser nulo para traer todo.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de rutas encontradas y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDtos.AbdRouteResponse>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "idRuta") Pageable pageable) {
        return ResponseEntity.ok(rutaAbdService.list(search, pageable));
    }

    /**
     * Atiende la peticion de consulta del detalle de una ruta existente.
     * @param id identificador de la ruta recibido en la ruta de la peticion.
     * @return respuesta con los datos de la ruta encontrada y estado 200.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AbdDtos.AbdRouteResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rutaAbdService.findById(id));
    }

    /**
     * Atiende la peticion de registro de una ruta nueva en el sistema.
     * @param request cuerpo de la peticion con los datos validados de terminales y precio por registrar.
     * @return respuesta con los datos de la ruta guardada y estado 201.
     */
    @PostMapping
    public ResponseEntity<AbdDtos.AbdRouteResponse> create(@Valid @RequestBody AbdDtos.AbdRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaAbdService.create(request));
    }

    /**
     * Atiende la peticion de modificacion de los datos de una ruta existente.
     * @param id identificador de la ruta recibido en la ruta de la peticion.
     * @param request cuerpo de la peticion con los nuevos datos validados de la ruta.
     * @return respuesta con los datos de la ruta actualizada y estado 200.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.AbdRouteResponse> update(@PathVariable Integer id,
                                                              @Valid @RequestBody AbdDtos.AbdRouteRequest request) {
        return ResponseEntity.ok(rutaAbdService.update(id, request));
    }

    /**
     * Atiende la peticion de supresion del registro de una ruta existente.
     * @param id identificador de la ruta recibido en la ruta de la peticion.
     * @return respuesta sin contenido y estado 204 cuando la supresion termina con exito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rutaAbdService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
