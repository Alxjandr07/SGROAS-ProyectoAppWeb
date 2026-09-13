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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/abd/conductores")
@RequiredArgsConstructor
public class ConductorAbdController {

    private final ConductorAbdRepository conductorAbdRepository;

    /**
     * Atiende la peticion de consulta paginada de conductores con busqueda opcional.
     * @param search texto para buscar coincidencias en los datos del conductor, puede ser nulo para traer todo.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de conductores encontrados y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<ConductorAbd>> listar(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "idConductor") Pageable pageable) {
        String filtro = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        Page<ConductorAbd> page = filtro == null
                ? conductorAbdRepository.findAll(pageable)
                : conductorAbdRepository.buscar(filtro, pageable);
        return ResponseEntity.ok(page);
    }
}
