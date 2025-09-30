package pe.com.farmaciadey.metodopago.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.com.farmaciadey.metodopago.models.Metodopago;
import pe.com.farmaciadey.metodopago.repository.MetodopagoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetodopagoServiceCrudTest {
    @Mock
    private MetodopagoRepository metodopagoRepository;

    @InjectMocks
    private MetodopagoService metodopagoService;

    public MetodopagoServiceCrudTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
        Metodopago m1 = new Metodopago();
        Metodopago m2 = new Metodopago();
        when(metodopagoRepository.list()).thenReturn(Arrays.asList(m1, m2));
        List<Metodopago> lista = metodopagoService.list();
        assertEquals(2, lista.size());
    }

    @Test
    void testSave() {
        Metodopago m = new Metodopago();
        when(metodopagoRepository.save(m)).thenReturn(m);
        Metodopago result = metodopagoService.save(m);
        assertNotNull(result);
    }

    @Test
    void testFind() {
        Metodopago m = new Metodopago();
        when(metodopagoRepository.find(1)).thenReturn(m);
        Metodopago result = metodopagoService.find(1);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        Metodopago m = new Metodopago();
        m.setId(1);
        m.setEliminado(0);
        when(metodopagoRepository.findById(1)).thenReturn(Optional.of(m));
        when(metodopagoRepository.save(any(Metodopago.class))).thenReturn(m);
        boolean deleted = metodopagoService.delete(1);
        assertTrue(deleted);
    }
}
