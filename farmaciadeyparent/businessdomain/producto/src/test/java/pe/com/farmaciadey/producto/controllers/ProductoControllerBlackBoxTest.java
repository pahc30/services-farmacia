package pe.com.farmaciadey.producto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.producto.services.ProductoService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductoController.class)
public class ProductoControllerBlackBoxTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Test
        void testSaveProducto_ReturnsDataResponse() throws Exception {
        // Simula el JSON de producto
        String productoJson = "{" +
            "\"nombre\": \"Paracetamol\"," +
            "\"precio\": 10.5," +
            "\"stock\": 100}";

        // Mock para que el servicio devuelva un producto válido
        pe.com.farmaciadey.producto.models.Producto productoMock = new pe.com.farmaciadey.producto.models.Producto();
        productoMock.setNombre("Paracetamol");
        productoMock.setPrecio(10.5);
        productoMock.setStock(100);
        org.mockito.Mockito.when(productoService.save(org.mockito.Mockito.any())).thenReturn(productoMock);

        mockMvc.perform(post("/api/producto/save-without-image")
            .contentType(MediaType.APPLICATION_JSON)
            .content(productoJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dato").exists()); // Verifica que el campo 'dato' esté en la respuesta
        }
}
