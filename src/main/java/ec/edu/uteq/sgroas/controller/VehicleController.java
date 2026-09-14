package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.VehicleRequest;
import ec.edu.uteq.sgroas.dto.VehicleResponse;
import ec.edu.uteq.sgroas.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehiculoService;

    /**
     * Recupera la lista paginada de vehículos registrados en el sistema.
     * @param pageable configuración de paginación y ordenamiento solicitada por el cliente.
     * @return respuesta HTTP con la página de vehículos encontrados.
     */
    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> list(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(vehiculoService.list(pageable));
    }

    /**
     * Obtiene el detalle de un vehículo a partir de su identificador.
     * @param id identificador único del vehículo que se desea consultar.
     * @return respuesta HTTP con los datos del vehículo encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.findById(id));
    }

    /**
     * Registra un nuevo vehículo con los datos recibidos en la solicitud.
     * @param request objeto con la placa, modelo y demás datos del vehículo.
     * @return respuesta HTTP con estado creado y los datos del vehículo registrado.
     */
    @PostMapping
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehiculoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Modifica los datos de un vehículo ya existente.
     * @param id identificador único del vehículo que se desea modificar.
     * @param request objeto con los nuevos valores para actualizar el vehículo.
     * @return respuesta HTTP con los datos actualizados del vehículo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request
    ) {
        return ResponseEntity.ok(vehiculoService.update(id, request));
    }

    /**
     * Desactiva lógicamente un vehículo para que deje de estar disponible.
     * @param id identificador único del vehículo que se desea desactivar.
     * @return respuesta HTTP sin contenido que confirma la operación realizada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        vehiculoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
