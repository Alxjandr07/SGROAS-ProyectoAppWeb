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

    @PostMapping
    public ResponseEntity<AbdDtos.ProgramacionResponse> crear(@Valid @RequestBody AbdDtos.ProgramacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programacionAbdService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbdDtos.ProgramacionResponse> actualizar(@PathVariable Integer id,
                                                                   @Valid @RequestBody AbdDtos.ProgramacionRequest request) {
        return ResponseEntity.ok(programacionAbdService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        programacionAbdService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
