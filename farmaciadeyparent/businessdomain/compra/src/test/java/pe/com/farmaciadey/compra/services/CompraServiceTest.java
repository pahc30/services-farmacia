package pe.com.farmaciadey.compra.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.models.DetalleCompra;
import pe.com.farmaciadey.compra.repository.CompraRepository;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @InjectMocks
    private CompraService compraService;

    @Mock
    private CompraRepository compraRepository;

    @Test
    void saveCompra_shouldCallRepositorySave() {
    Compra compra = new Compra();
    compra.setSubtotal(100.0);
    compra.setIgv(0.18);
        Mockito.when(compraRepository.save(Mockito.any())).thenReturn(compra);
        Compra result = compraService.save(compra);
        assert result != null;
        Mockito.verify(compraRepository).save(compra);
    }

    @Test
    void listByUsuario_shouldReturnList() {
        Compra compra = new Compra();
    Mockito.when(compraRepository.list(Mockito.anyInt())).thenReturn(List.of(compra));
    List<Compra> result = compraService.listByUsuario(1);
    assert result.size() == 1;
    Mockito.verify(compraRepository).list(1);
    }
}
