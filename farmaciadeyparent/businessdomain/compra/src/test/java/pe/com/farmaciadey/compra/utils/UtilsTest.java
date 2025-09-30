package pe.com.farmaciadey.compra.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void testRequestOkConstant() {
        assertEquals(1, Utils.REQUEST_OK);
    }

    @Test
    void testRequestErrorConstant() {
        assertEquals(0, Utils.REQUEST_ERROR);
    }

    @Test
    void testBaseUrlProductoConstant() {
        assertNotNull(Utils.BASE_URL_PRODUCTO);
        assertTrue(Utils.BASE_URL_PRODUCTO.contains("producto"));
    }

    @Test
    void testBaseUrlMetodoPagoConstant() {
        assertNotNull(Utils.BASE_URL_METODO_PAGO);
        assertTrue(Utils.BASE_URL_METODO_PAGO.contains("metodopago"));
    }

    @Test
    void testPrivateConstructorCoverage() throws Exception {
        java.lang.reflect.Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
