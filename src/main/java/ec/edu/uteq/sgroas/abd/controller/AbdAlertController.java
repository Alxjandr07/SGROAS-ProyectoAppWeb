package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.entity.Alert;
import ec.edu.uteq.sgroas.abd.repository.AlertRepository;
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
public class AbdAlertController {

    private final AlertRepository alertaRepository;

    /**
     * Atiende la peticion de consulta paginada de las alertas generadas por incidentes de riesgo alto.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de alertas encontradas y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDtos.AlertaResponse>> listar(
            @PageableDefault(size = 50, sort = "idAlerta") Pageable pageable) {
        Page<Alert> page = alertaRepository.findAll(pageable);
        return ResponseEntity.ok(page.map(a -> new AbdDtos.AlertaResponse(
                a.getIdAlerta(), a.getNivelRiesgo(), a.getDescripcion(),
                a.getFecha() != null ? a.getFecha().toString() : null,
                a.getIncidente() != null ? a.getIncidente().getIdIncidente() : null,
                a.getIncidente() != null ? a.getIncidente().getTipo() : null)));
    }
}
