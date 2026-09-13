package ec.edu.uteq.sgroas.abd.controller;

import ec.edu.uteq.sgroas.abd.dto.AbdDtos;
import ec.edu.uteq.sgroas.abd.service.CatalogoAbdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/abd/catalogos")
@RequiredArgsConstructor
public class CatalogoAbdController {

    private final CatalogoAbdService catalogoAbdService;

    /**
     * Atiende la peticion de consulta de los catalogos generales del sistema.
     * @return respuesta con las listas de provincias, ciudades, terminales y roles y estado 200.
     */
    @GetMapping
    public ResponseEntity<AbdDtos.CatalogosResponse> catalogos() {
        return ResponseEntity.ok(catalogoAbdService.obtenerCatalogos());
    }
}
