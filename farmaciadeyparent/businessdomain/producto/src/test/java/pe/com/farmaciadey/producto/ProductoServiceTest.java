package pe.com.farmaciadey.producto;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.com.farmaciadey.producto.models.Producto;
import pe.com.farmaciadey.producto.services.ProductoService;
import pe.com.farmaciadey.producto.repository.ProductoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    public ProductoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
           Producto p1 = new Producto();
           Producto p2 = new Producto();
           when(productoRepository.list()).thenReturn(Arrays.asList(p1, p2));
           List<Producto> productos = productoService.list();
           assertEquals(2, productos.size());
    }

    @Test
    void testSave() {
           Producto p = new Producto();
           when(productoRepository.save(p)).thenReturn(p);
           Producto result = productoService.save(p);
           assertNotNull(result);
    }

    @Test
    void testFind() {
           Producto p = new Producto();
           when(productoRepository.find(1)).thenReturn(p);
           Producto result = productoService.find(1);
           assertNotNull(result);
    }

    @Test
    void testDelete() {
           Producto p = new Producto();
           p.setId(1);
           p.setEliminado(0);
           when(productoRepository.findById(1)).thenReturn(Optional.of(p));
           when(productoRepository.save(any(Producto.class))).thenReturn(p);
           boolean deleted = productoService.delete(1);
           assertTrue(deleted);
    }
}
