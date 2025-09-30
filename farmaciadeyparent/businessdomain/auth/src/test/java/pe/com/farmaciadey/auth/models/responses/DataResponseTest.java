package pe.com.farmaciadey.auth.models.responses;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataResponseTest {
    @Test
    void testDefaultConstructor() {
        DataResponse dr = new DataResponse();
        assertNull(dr.getDato());
        assertNull(dr.getMensaje());
        assertNull(dr.getEstado());
    }

    @Test
    void testDatoConstructor() {
        DataResponse dr = new DataResponse("data");
        assertEquals("data", dr.getMensaje());
        assertNull(dr.getDato());
        assertNull(dr.getEstado());
    }

    @Test
    void testDatoEstadoConstructor() {
        DataResponse dr = new DataResponse("data", 1);
        assertEquals("data", dr.getMensaje());
        assertEquals(1, dr.getEstado());
        assertNull(dr.getDato());
    }

    @Test
    void testObjectConstructor() {
        DataResponse dr = new DataResponse(123);
        assertEquals(123, dr.getDato());
        assertNull(dr.getMensaje());
        assertNull(dr.getEstado());
    }

    @Test
    void testObjectEstadoConstructor() {
        DataResponse dr = new DataResponse(123, 1);
        assertEquals(123, dr.getDato());
        assertEquals(1, dr.getEstado());
        assertNull(dr.getMensaje());
    }

    @Test
    void testFullConstructor() {
        DataResponse dr = new DataResponse(123, "msg", 1);
        assertEquals(123, dr.getDato());
        assertEquals("msg", dr.getMensaje());
        assertEquals(1, dr.getEstado());
    }
}
