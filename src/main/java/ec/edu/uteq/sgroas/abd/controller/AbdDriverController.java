package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.entity.AbdDriver;
import ec.edu.uteq.sgroas.abd.repository.AbdDriverRepository;
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
public class AbdDriverController {

    private final AbdDriverRepository conductorAbdRepository;

    /**
     * Atiende la peticion de consulta paginada de conductores con busqueda opcional.
     * @param search texto para buscar coincidencias en los datos del conductor, puede ser nulo para traer todo.
     * @param pageable configuracion de paginacion y orden enviada por el cliente.
     * @return respuesta con la pagina de conductores encontrados y estado 200.
     */
    @GetMapping
    public ResponseEntity<Page<AbdDriver>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "idConductor") Pageable pageable) {
        String filtro = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        Page<AbdDriver> page = filtro == null
                ? conductorAbdRepository.findAll(pageable)
                : conductorAbdRepository.search(filtro, pageable);
        return ResponseEntity.ok(page);
    }
}
