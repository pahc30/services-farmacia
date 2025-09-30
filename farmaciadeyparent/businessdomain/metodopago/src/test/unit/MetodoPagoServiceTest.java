package pe.com.farmaciadey.metodopago.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import pe.com.farmaciadey.metodopago.service.MetodoPagoService; // ajusta package

class MetodoPagoServiceTest {

  private final MetodoPagoService service = new MetodoPagoService(); // si requiere deps, usa @ExtendWith + @InjectMocks

  @ParameterizedTest
  @CsvSource({
      "TARJETA,100.00,3.90",
      "YAPE,50.00,1.25",
      "EFECTIVO,80.00,0.00"
  })
  void calcularComision_porTipo(String tipo, double monto, double esperado) {
    double comision = service.calcularComision(tipo, monto);
    assertEquals(esperado, comision, 0.0001);
  }

  @Test
  void calcularComision_tipoDesconocido_retornaCero() {
    double c = service.calcularComision("CRIPTO", 100.0);
    assertEquals(0.0, c, 0.0001);
  }
}
