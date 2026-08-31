package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.Alerta;
import ec.edu.uteq.sgroas.abd.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/abd/alertas")
@RequiredArgsConstructor
public class AlertaAbdController {

    private final AlertaRepository alertaRepository;

    @GetMapping
    public ResponseEntity<Page<AbdDtos.AlertaResponse>> listar(
            @PageableDefault(size = 50, sort = "idAlerta") Pageable pageable) {
        Page<Alerta> page = alertaRepository.findAll(pageable);
        return ResponseEntity.ok(page.map(a -> new AbdDtos.AlertaResponse(
                a.getIdAlerta(), a.getNivelRiesgo(), a.getDescripcion(),
                a.getFecha() != null ? a.getFecha().toString() : null,
                a.getIncidente() != null ? a.getIncidente().getIdIncidente() : null,
                a.getIncidente() != null ? a.getIncidente().getTipo() : null)));
    }
}
