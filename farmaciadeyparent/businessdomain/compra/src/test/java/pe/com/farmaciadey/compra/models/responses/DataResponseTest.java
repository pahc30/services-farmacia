package pe.com.farmaciadey.compra.models.responses;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pe.com.farmaciadey.compra.utils.Utils;

class DataResponseTest {
    @Test
    void testDefaultConstructor() {
        DataResponse dr = new DataResponse();
        assertNull(dr.getDato());
        assertNull(dr.getMensaje());
        assertEquals(Utils.REQUEST_OK, dr.getEstado());
    }

    @Test
    void testDatoConstructor() {
        DataResponse dr = new DataResponse("data");
        assertNull(dr.getDato());
        assertEquals("data", dr.getMensaje());
        assertEquals(Utils.REQUEST_OK, dr.getEstado());
    }

    @Test
    void testDatoEstadoConstructor() {
        DataResponse dr = new DataResponse("data", 1);
        assertNull(dr.getDato());
        assertEquals("data", dr.getMensaje());
        assertEquals(1, dr.getEstado());
    }

    @Test
    void testObjectConstructor() {
        DataResponse dr = new DataResponse(123);
        assertEquals(123, dr.getDato());
        assertNull(dr.getMensaje());
        assertEquals(Utils.REQUEST_OK, dr.getEstado());
    }

    @Test
    void testObjectEstadoConstructor() {
        DataResponse dr = new DataResponse(123, 1);
        assertEquals(123, dr.getDato());
        assertNull(dr.getMensaje());
        assertEquals(1, dr.getEstado());
    }

    @Test
    void testFullConstructor() {
        DataResponse dr = new DataResponse(123, "msg", 1);
        assertEquals(123, dr.getDato());
        assertEquals("msg", dr.getMensaje());
        assertEquals(1, dr.getEstado());
    }

    @Test
    void testSetException() {
        DataResponse dr = new DataResponse();
        dr.setException("error");
        assertEquals("Error interno. error", dr.getMensaje());
        assertNull(dr.getDato());
        assertEquals(Utils.REQUEST_ERROR, dr.getEstado());
    }
}
