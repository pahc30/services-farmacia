package pe.com.farmaciadey.compra.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import pe.com.farmaciadey.compra.controllers.MockedCarritoCompraControllerConfig;
import pe.com.farmaciadey.compra.controllers.WebClientBuilderTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.models.DetalleCompra;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import pe.com.farmaciadey.compra.services.CompraService;
import pe.com.farmaciadey.compra.services.CompraIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Collections;
import java.util.UUID;
import java.util.List;

@WebMvcTest(CompraController.class)
@Import({MockedCarritoCompraControllerConfig.class, WebClientBuilderTestConfig.class})
class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompraService compraService;

    @MockBean
    private CarritoCompraService carritoCompraService;

    @MockBean
    private CompraIntegrationService integrationService;

    @Test
    @DisplayName("Debería retornar error si producto no existe")
    void save_shouldReturnErrorIfProductNotFound() throws Exception {
        // Arrange
        Compra compra = new Compra();
        DetalleCompra detalle = new DetalleCompra();
        detalle.setProductoId(999); // ID inexistente
        compra.setDetalleCompra(List.of(detalle));

    Mockito.when(integrationService.getProducto(Mockito.anyInt())).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/compra/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(compra)))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("El producto no existe")));
    }

    @Test
    @DisplayName("Debería retornar error si stock insuficiente")
    void save_shouldReturnErrorIfStockInsufficient() throws Exception {
        // Arrange
        Compra compra = new Compra();
        DetalleCompra detalle = new DetalleCompra();
        detalle.setProductoId(1);
        detalle.setCantidad(10); // Más que el stock disponible
        compra.setDetalleCompra(List.of(detalle));

    ProductoResponse producto = new ProductoResponse();
    producto.setId(1);
    producto.setStock(5); // Stock insuficiente
    producto.setPrecio(10.0);

    Mockito.when(integrationService.getProducto(Mockito.anyInt())).thenReturn(producto);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/compra/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(compra)))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("El producto no cuenta con stock suficiente")));
    }

    @Test
    @DisplayName("Debería guardar compra correctamente")
    void save_shouldSaveCompraSuccessfully() throws Exception {
        // Arrange
        Compra compra = new Compra();
        DetalleCompra detalle = new DetalleCompra();
        detalle.setProductoId(1);
        detalle.setCantidad(2);
        compra.setDetalleCompra(List.of(detalle));

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1);
        producto.setStock(10);
        producto.setPrecio(5.0);

    Mockito.when(integrationService.getProducto(Mockito.anyInt())).thenReturn(producto);
    Mockito.when(compraService.save(Mockito.any())).thenReturn(compra);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/compra/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(compra)))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.dato").exists());
    }

    @Test
    @DisplayName("Debería listar compras por usuario")
    void list_shouldReturnComprasByUsuario() throws Exception {
        // Arrange
        Compra compra = new Compra();
        compra.setMetodoPago("Tarjeta");
        compra.setDetalleCompra(Collections.emptyList());
        List<Compra> compras = List.of(compra);

    Mockito.when(compraService.listByUsuario(Mockito.anyInt())).thenReturn(compras);
    Mockito.when(integrationService.getMetodoPago(Mockito.anyInt())).thenReturn("Tarjeta");
    Mockito.when(integrationService.getProducto(Mockito.anyInt())).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/compra/list/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.dato").isArray());
    }
}
