package pe.com.farmaciadey.usuario.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.usuario.services.UsuarioService;
import pe.com.farmaciadey.usuario.models.Usuario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerBlackBoxTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;
    @Test
    void testSaveUsuario_ReturnsDataResponse() throws Exception {
        String usuarioJson = "{"
            + "\"nombres\": \"Juan Perez\"," 
            + "\"apellidos\": \"Perez\"," 
            + "\"email\": \"juan@example.com\"," 
            + "\"password\": \"123456\"," 
            + "\"username\": \"juanperez\"," 
            + "\"identificacion\": \"12345678\"," 
            + "\"rol\": \"USER\"}";

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombres("Juan Perez");
        usuarioMock.setApellidos("Perez");
        usuarioMock.setEmail("juan@example.com");
        usuarioMock.setPassword("123456");
        usuarioMock.setUsername("juanperez");
        usuarioMock.setIdentificacion("12345678");
        usuarioMock.setRol("USER");

        org.mockito.Mockito.when(usuarioService.save(org.mockito.Mockito.any(Usuario.class))).thenReturn(usuarioMock);

        mockMvc.perform(post("/api/usuario/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dato").exists());
    }
    // ...existing code...
}
