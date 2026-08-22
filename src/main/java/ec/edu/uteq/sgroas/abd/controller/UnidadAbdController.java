package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.UnidadAbdService;
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
public class UnidadAbdController {

    private final UnidadAbdService unidadAbdService;

    @GetMapping
    public ResponseEntity<Page<AbdDtos.UnidadResponse>> listar(
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 50, sort = "idUnidad") Pageable pageable) {
        return ResponseEntity.ok(unidadAbdService.listar(estado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbdDtos.UnidadResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(unidadAbdService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AbdDtos.UnidadResponse> crear(@Valid @RequestBody AbdDtos.UnidadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadAbdService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.UnidadResponse> actualizar(@PathVariable Integer id,
                                                             @Valid @RequestBody AbdDtos.UnidadRequest request) {
        return ResponseEntity.ok(unidadAbdService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        unidadAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
