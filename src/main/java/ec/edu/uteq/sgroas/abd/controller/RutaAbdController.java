package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.RutaAbdService;
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
public class RutaAbdController {

    private final RutaAbdService rutaAbdService;

    @GetMapping
    public ResponseEntity<Page<AbdDtos.RutaAbdResponse>> listar(
            @PageableDefault(size = 50, sort = "idRuta") Pageable pageable) {
        return ResponseEntity.ok(rutaAbdService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbdDtos.RutaAbdResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rutaAbdService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AbdDtos.RutaAbdResponse> crear(@Valid @RequestBody AbdDtos.RutaAbdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaAbdService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.RutaAbdResponse> actualizar(@PathVariable Long id,
                                                              @Valid @RequestBody AbdDtos.RutaAbdRequest request) {
        return ResponseEntity.ok(rutaAbdService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
