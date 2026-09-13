package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.VehicleResponse;
import ec.edu.uteq.sgroas.service.VehicleService;
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
class VehiculoControllerTest {

    @Mock
    private VehicleService vehiculoService;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(new VehicleController(vehiculoService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        Jackson2ObjectMapperBuilder.json()
                                .modulesToInstall(new SpringDataJacksonConfiguration.PageModule(
                                        new SpringDataWebSettings(PageSerializationMode.DIRECT)))
                                .build()))
                .build();
    }

    private VehicleResponse responseEjemplo() {
        return new VehicleResponse(
                1L, "GTU-001", "Toyota", "Hiace", 2020, 14,
                "MOT-123", "CHAS-123", "Blanco", "ACTIVO", true,
                Instant.now(), Instant.now()
        );
    }

    @Test
    void listarDebeRetornar200() throws Exception {
        when(vehiculoService.listar(any()))
                .thenReturn(new PageImpl<>(List.of(responseEjemplo())));

        mockMvc().perform(get("/api/vehiculos"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorIdDebeRetornar200() throws Exception {
        when(vehiculoService.buscarPorId(1L)).thenReturn(responseEjemplo());

        mockMvc().perform(get("/api/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    void crearDebeRetornar201() throws Exception {
        when(vehiculoService.crear(any())).thenReturn(responseEjemplo());

        mockMvc().perform(post("/api/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "GTU-001",
                                  "marca": "Toyota",
                                  "modelo": "Hiace",
                                  "anio": 2020,
                                  "capacidadPasajeros": 14,
                                  "numeroMotor": "MOT-123",
                                  "numeroChasis": "CHAS-123",
                                  "color": "Blanco",
                                  "estado": "ACTIVO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarDebeRetornar200() throws Exception {
        when(vehiculoService.actualizar(any(), any())).thenReturn(responseEjemplo());

        mockMvc().perform(put("/api/vehiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "GTU-001",
                                  "marca": "Toyota",
                                  "modelo": "Hiace",
                                  "anio": 2020,
                                  "capacidadPasajeros": 14,
                                  "numeroMotor": "MOT-123",
                                  "numeroChasis": "CHAS-123",
                                  "color": "Blanco",
                                  "estado": "ACTIVO"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarDebeRetornar204() throws Exception {
        mockMvc().perform(delete("/api/vehiculos/1"))
                .andExpect(status().isNoContent());
    }
}
