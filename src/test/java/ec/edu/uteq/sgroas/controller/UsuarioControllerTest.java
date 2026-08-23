package ec.edu.uteq.sgroas.controller;

import ec.edu.uteq.sgroas.dto.UsuarioResponse;
import ec.edu.uteq.sgroas.service.UsuarioService;
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
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        Jackson2ObjectMapperBuilder.json()
                                .modulesToInstall(new SpringDataJacksonConfiguration.PageModule(
                                        new SpringDataWebSettings(PageSerializationMode.DIRECT)))
                                .build()))
                .build();
    }

    private UsuarioResponse responseEjemplo() {
        return new UsuarioResponse(
                1L, "Administrador SGROAS", "admin@sgroas.com",
                "ROLE_ADMIN", true, Instant.now(), Instant.now()
        );
    }

    @Test
    void listarDebeRetornar200() throws Exception {
        when(usuarioService.listar(any(), any()))
                .thenReturn(new PageImpl<>(List.of(responseEjemplo())));

        mockMvc().perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorIdDebeRetornar200() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(responseEjemplo());

        mockMvc().perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@sgroas.com"));
    }

    @Test
    void crearDebeRetornar201() throws Exception {
        when(usuarioService.crear(any())).thenReturn(responseEjemplo());

        mockMvc().perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Administrador SGROAS",
                                  "email": "admin@sgroas.com",
                                  "password": "123456",
                                  "rol": "ROLE_ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarDebeRetornar200() throws Exception {
        when(usuarioService.actualizar(any(), any())).thenReturn(responseEjemplo());

        mockMvc().perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Administrador SGROAS",
                                  "email": "admin@sgroas.com",
                                  "password": "123456",
                                  "rol": "ROLE_ADMIN"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarDebeRetornar204() throws Exception {
        mockMvc().perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}
