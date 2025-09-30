package pe.com.farmaciadey.metodopago.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.metodopago.services.MetodopagoService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MetodoPagoController.class)
public class MetodoPagoControllerBlackBoxTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetodopagoService metodopagoService;

    @Test
        void testSaveMetodoPago_ReturnsDataResponse() throws Exception {
            // Simula el JSON de método de pago
            String metodoPagoJson = "{" +
                "\"tipo\": \"Tarjeta\"," +
                "\"descripcion\": \"Pago con tarjeta\"}";

            // Mock para que el servicio devuelva un método de pago válido
            pe.com.farmaciadey.metodopago.models.Metodopago metodoMock = new pe.com.farmaciadey.metodopago.models.Metodopago();
            metodoMock.setTipo("Tarjeta");
            metodoMock.setDescripcion("Pago con tarjeta");
            org.mockito.Mockito.when(metodopagoService.save(org.mockito.Mockito.any())).thenReturn(metodoMock);

            mockMvc.perform(post("/api/metodopago/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(metodoPagoJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.dato").exists()); // Verifica que el campo 'dato' esté en la respuesta
        }
}
