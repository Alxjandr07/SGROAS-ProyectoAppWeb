package ec.edu.uteq.sgroas.abd.controller;

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
    public ResponseEntity<Page<Alerta>> listar(
            @PageableDefault(size = 50, sort = "idAlerta") Pageable pageable) {
        return ResponseEntity.ok(alertaRepository.findAll(pageable));
    }
}
