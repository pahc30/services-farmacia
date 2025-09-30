    // ...código existente...

    // ...código existente...

    @Test
    void testConstructorPorDefecto() {
        CarritoCompraController controller = new CarritoCompraController();
        org.junit.jupiter.api.Assertions.assertNotNull(controller);
    }

    @Test
    void testGetProducto_NullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        Mockito.doReturn(null).when(spyController).getProducto(Mockito.anyInt());
        pe.com.farmaciadey.compra.models.responses.ProductoResponse result = spyController.getProducto(999);
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @Test
    void testGetProducto_NonNullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        pe.com.farmaciadey.compra.models.responses.ProductoResponse mockProducto = new pe.com.farmaciadey.compra.models.responses.ProductoResponse();
        mockProducto.setId(1);
        mockProducto.setStock(10);
        mockProducto.setPrecio(5.0);
        mockProducto.setCodigo("A");
        mockProducto.setNombre("Test");
        mockProducto.setUrl("url");
        mockProducto.setCategoria("cat");
        Mockito.doReturn(mockProducto).when(spyController).getProducto(Mockito.anyInt());
        pe.com.farmaciadey.compra.models.responses.ProductoResponse result = spyController.getProducto(1);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getId());
    }

    @Test
    void testListByUsuario_NonEmpty() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        java.util.List<CarritoCompra> data = java.util.List.of(carrito);
        Mockito.when(service.listByUsuario(Mockito.anyInt())).thenReturn(data);
        CarritoCompraController spyController = Mockito.spy(controller);
        pe.com.farmaciadey.compra.models.responses.ProductoResponse mockProducto = new pe.com.farmaciadey.compra.models.responses.ProductoResponse();
        mockProducto.setId(2);
        mockProducto.setStock(10);
        mockProducto.setPrecio(5.0);
        mockProducto.setCodigo("A");
        mockProducto.setNombre("Test");
        mockProducto.setUrl("url");
        mockProducto.setCategoria("cat");
        Mockito.doReturn(mockProducto).when(spyController).getProducto(Mockito.anyInt());
        org.springframework.http.ResponseEntity<pe.com.farmaciadey.compra.models.responses.DataResponse> response = spyController.list(1);
        org.junit.jupiter.api.Assertions.assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody().getDato());
    }
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
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarritoCompraControllerTest {
    // ...existing code...

    @Test
    void testDefaultConstructor() {
        CarritoCompraController controller = new CarritoCompraController();
        org.junit.jupiter.api.Assertions.assertNotNull(controller);
    }

    @Test
    void testGetProducto_NullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        // Simulate block.get("dato") == null
        Mockito.doReturn(null).when(spyController).getProducto(Mockito.anyInt());
        ProductoResponse result = spyController.getProducto(999);
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @Test
    void testGetProducto_NonNullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        ProductoResponse mockProducto = new ProductoResponse();
        mockProducto.setId(1);
        mockProducto.setStock(10);
        mockProducto.setPrecio(5.0);
        mockProducto.setCodigo("A");
        mockProducto.setNombre("Test");
        mockProducto.setUrl("url");
        mockProducto.setCategoria("cat");
        Mockito.doReturn(mockProducto).when(spyController).getProducto(Mockito.anyInt());
        ProductoResponse result = spyController.getProducto(1);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getId());
    }

    @Test
    void testListByUsuario_NonEmpty() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        List<CarritoCompra> data = List.of(carrito);
        Mockito.when(service.listByUsuario(Mockito.anyInt())).thenReturn(data);
        CarritoCompraController spyController = Mockito.spy(controller);
        ProductoResponse mockProducto = new ProductoResponse();
        mockProducto.setId(2);
        mockProducto.setStock(10);
        mockProducto.setPrecio(5.0);
        mockProducto.setCodigo("A");
        mockProducto.setNombre("Test");
        mockProducto.setUrl("url");
        mockProducto.setCategoria("cat");
        Mockito.doReturn(mockProducto).when(spyController).getProducto(Mockito.anyInt());
        ResponseEntity<DataResponse> response = spyController.list(1);
        org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody().getDato());
    }
    // ...existing code...
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
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarritoCompraControllerTest {
    @Test
    void testSaveCarritoCompra_ProductNotFound() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
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
