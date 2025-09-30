package pe.com.farmaciadey.compra.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.compra.services.CompraService;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import pe.com.farmaciadey.compra.services.CompraIntegrationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CompraController.class)
public class CompraControllerBlackBoxTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompraService compraService;

    @MockBean
    private CarritoCompraService carritoCompraService;

    @MockBean
    private CompraIntegrationService compraIntegrationService;

    @Test
    void testSaveCompra_ReturnsDataResponse() throws Exception {
        // Simula el JSON de compra
        String compraJson = "{" +
                "\"usuarioId\": 1," +
                "\"total\": 100.0," +
                "\"productos\": []}";

        mockMvc.perform(post("/api/compra/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists()); // Verifica que el campo 'success' esté en la respuesta
    }
}
