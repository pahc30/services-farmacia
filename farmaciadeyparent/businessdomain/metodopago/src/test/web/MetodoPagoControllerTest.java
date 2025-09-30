package pe.com.farmaciadey.metodopago.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pe.com.farmaciadey.metodopago.controllers.MetodopagoController;
import pe.com.farmaciadey.metodopago.services.MetodopagoService;

@WebMvcTest(controllers = MetodopagoController.class)
class MetodopagoControllerTest {

  @Autowired private MockMvc mvc;
  @MockBean private MetodopagoService service;

  @Test
  void calcular_ok_200() throws Exception {
    when(service.calcularComision("TARJETA", 100.0)).thenReturn(3.90);

  mvc.perform(post("/metodopago/calcular")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"tipo\":\"TARJETA\",\"monto\":100.0}"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.comision").value(3.90));
  }

  @Test
  void calcular_payloadInvalido_400() throws Exception {
  mvc.perform(post("/metodopago/calcular")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"monto\":100.0}")) // falta "tipo"
      .andExpect(status().isBadRequest());
  }
}
