package pe.com.farmaciadey.usuario.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.usuario.models.Usuario;
import pe.com.farmaciadey.usuario.services.UsuarioService;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSaveSuccess() throws Exception {
        Usuario u = new Usuario();
        Mockito.when(service.save(any(Usuario.class))).thenReturn(u);
        mockMvc.perform(post("/api/usuario/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dato").exists());
    }

    @Test
    void testSaveError() throws Exception {
        Usuario u = new Usuario();
        Mockito.when(service.save(any(Usuario.class))).thenThrow(new Exception("error"));
        mockMvc.perform(post("/api/usuario/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        Usuario u = new Usuario();
        u.setId(1);
        Mockito.when(service.update(any(Usuario.class))).thenReturn(u);
        mockMvc.perform(post("/api/usuario/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dato").exists());
    }

    @Test
    void testUpdateBadRequest() throws Exception {
        Usuario u = new Usuario();
        u.setId(2);
        mockMvc.perform(post("/api/usuario/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("No se pudo actualizar el registro")));
    }

    @Test
    void testListSuccess() throws Exception {
        Mockito.when(service.list()).thenReturn(Collections.emptyList());
        mockMvc.perform(post("/api/usuario/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dato").isArray());
    }

    @Test
    void testFindSuccess() throws Exception {
        Usuario u = new Usuario();
        Mockito.when(service.find(eq(1))).thenReturn(u);
        mockMvc.perform(post("/api/usuario/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dato").exists());
    }

    @Test
    void testDeleteSuccess() throws Exception {
        Mockito.when(service.delete(eq(1))).thenReturn(true);
        mockMvc.perform(post("/api/usuario/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dato").value(true));
    }
}
