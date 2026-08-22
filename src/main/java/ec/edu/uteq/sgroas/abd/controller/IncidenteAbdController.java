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

    @GetMapping
    public ResponseEntity<Page<AbdDtos.IncidenteAbdResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nivel,
            @PageableDefault(size = 50, sort = "idIncidente") Pageable pageable) {
        return ResponseEntity.ok(incidenteAbdService.listar(estado, nivel, pageable));
    }

    @PostMapping
    public ResponseEntity<AbdDtos.IncidenteAbdResponse> crear(@Valid @RequestBody AbdDtos.IncidenteAbdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenteAbdService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        incidenteAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
