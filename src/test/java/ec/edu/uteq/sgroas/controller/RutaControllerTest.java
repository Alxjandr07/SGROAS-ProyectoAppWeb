package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.RouteResponse;
import ec.edu.uteq.sgroas.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RutaControllerTest {

    @Mock
    private RouteService rutaService;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(new RouteController(rutaService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        Jackson2ObjectMapperBuilder.json()
                                .modulesToInstall(new SpringDataJacksonConfiguration.PageModule(
                                        new SpringDataWebSettings(PageSerializationMode.DIRECT)))
                                .build()))
                .build();
    }

    private RouteResponse responseEjemplo() {
        return new RouteResponse(
                1L, "R-001", "Quito - Guayaquil", "Quito", "Guayaquil",
                420.0, 480, "ACTIVA", true, Instant.now(), Instant.now()
        );
    }

    @Test
    void listarDebeRetornar200() throws Exception {
        when(rutaService.list(any()))
                .thenReturn(new PageImpl<>(List.of(responseEjemplo())));

        mockMvc().perform(get("/api/rutas"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorIdDebeRetornar200() throws Exception {
        when(rutaService.findById(1L)).thenReturn(responseEjemplo());

        mockMvc().perform(get("/api/rutas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Quito - Guayaquil"));
    }

    @Test
    void crearDebeRetornar201() throws Exception {
        when(rutaService.create(any())).thenReturn(responseEjemplo());

        mockMvc().perform(post("/api/rutas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "R-001",
                                  "nombre": "Quito - Guayaquil",
                                  "origen": "Quito",
                                  "destino": "Guayaquil",
                                  "distanciaKm": 420.0,
                                  "duracionEstimadaMin": 480,
                                  "estado": "ACTIVA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarDebeRetornar200() throws Exception {
        when(rutaService.update(any(), any())).thenReturn(responseEjemplo());

        mockMvc().perform(put("/api/rutas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "R-001",
                                  "nombre": "Quito - Guayaquil",
                                  "origen": "Quito",
                                  "destino": "Guayaquil",
                                  "distanciaKm": 420.0,
                                  "duracionEstimadaMin": 480,
                                  "estado": "ACTIVA"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarDebeRetornar204() throws Exception {
        mockMvc().perform(delete("/api/rutas/1"))
                .andExpect(status().isNoContent());
    }
}
