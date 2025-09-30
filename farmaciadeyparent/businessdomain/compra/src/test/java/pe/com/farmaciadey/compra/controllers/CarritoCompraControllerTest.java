package pe.com.farmaciadey.compra.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.mockito.Mockito;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.compra.models.CarritoCompra;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.models.responses.DataResponse;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarritoCompraControllerTest {

        carrito.setProductoId(2);
        carrito.setCantidad(3);
        CarritoCompraController spyController = Mockito.spy(controller);
        Mockito.doReturn(null).when(spyController).getProducto(Mockito.anyInt());
        ResponseEntity<?> response = spyController.save(carrito);
        org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testSaveCarritoCompra_InsufficientStock() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(10);
        CarritoCompraController spyController = Mockito.spy(controller);
        var producto = new pe.com.farmaciadey.compra.models.responses.ProductoResponse();
        producto.setId(2);
        producto.setStock(5);
        Mockito.doReturn(producto).when(spyController).getProducto(Mockito.anyInt());
        ResponseEntity<?> response = spyController.save(carrito);
        org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testSaveCarritoCompra_Exception() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        Mockito.when(service.save(Mockito.any())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/carrito/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(carrito))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testListByUsuario_Exception() throws Exception {
        Mockito.when(service.listByUsuario(Mockito.anyInt())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/carrito/list/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteCarritoCompra_Exception() throws Exception {
        Mockito.when(service.delete(Mockito.anyInt())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/carrito/delete/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    @Autowired
    private MockMvc mockMvc;

    private CarritoCompraService service = Mockito.mock(CarritoCompraService.class);

    @Autowired
    private CarritoCompraController controller;

    @Autowired
    public void setController(CarritoCompraController controller) {
        this.controller = controller;
        // Inject mock service into controller
        try {
            java.lang.reflect.Field serviceField = CarritoCompraController.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            serviceField.set(controller, service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveCarritoCompra() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        when(service.save(any(CarritoCompra.class))).thenReturn(carrito);
    mockMvc.perform(post("/api/carrito/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(carrito))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.dato.id").value(1));
    }

    @Test
    void testListByUsuario() throws Exception {
    mockMvc.perform(post("/api/carrito/list/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testDeleteCarritoCompra() throws Exception {
    mockMvc.perform(post("/api/carrito/delete/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }
}
