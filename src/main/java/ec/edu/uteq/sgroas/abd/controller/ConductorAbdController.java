package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.entity.ConductorAbd;
import ec.edu.uteq.sgroas.abd.repository.ConductorAbdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/abd/conductores")
@RequiredArgsConstructor
public class ConductorAbdController {

    private final ConductorAbdRepository conductorAbdRepository;

    @GetMapping
    public ResponseEntity<Page<ConductorAbd>> listar(
            @PageableDefault(size = 50, sort = "idConductor") Pageable pageable) {
        return ResponseEntity.ok(conductorAbdRepository.findAll(pageable));
    }
}
