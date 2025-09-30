package pe.com.farmaciadey.compra.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.farmaciadey.compra.models.CarritoCompra;
import pe.com.farmaciadey.compra.repository.CarritoCompraRepository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CarritoCompraServiceTest {
    @Mock
    CarritoCompraRepository repository;

    @InjectMocks
    CarritoCompraService service;

    @Test
    void testSaveNewCarritoCompra() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        when(repository.find(1, 2)).thenReturn(null);
        when(repository.save(carrito)).thenReturn(carrito);
        CarritoCompra result = service.save(carrito);
        assertEquals(carrito, result);
    }

    @Test
    void testSaveExistingCarritoCompra() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(5);
        CarritoCompra existing = new CarritoCompra();
        existing.setUsuarioId(1);
        existing.setProductoId(2);
        existing.setCantidad(3);
        when(repository.find(1, 2)).thenReturn(existing);
        when(repository.save(existing)).thenReturn(existing);
        CarritoCompra result = service.save(carrito);
        assertEquals(existing, result);
        assertEquals(5, existing.getCantidad());
    }

    @Test
    void testListByUsuario() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setUsuarioId(1);
        when(repository.list(1)).thenReturn(Arrays.asList(carrito));
        List<CarritoCompra> result = service.listByUsuario(1);
        assertEquals(1, result.size());
        assertEquals(carrito, result.get(0));
    }

    @Test
    void testDeleteFound() {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(10);
        carrito.setEliminado(0);
        when(repository.findById(10)).thenReturn(Optional.of(carrito));
        when(repository.save(carrito)).thenReturn(carrito);
        boolean result = service.delete(10);
        assertTrue(result);
        assertEquals(1, carrito.getEliminado());
    }

    @Test
    void testDeleteNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        boolean result = service.delete(99);
        assertFalse(result);
    }
}
