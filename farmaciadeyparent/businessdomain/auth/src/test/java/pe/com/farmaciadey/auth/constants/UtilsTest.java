package pe.com.farmaciadey.auth.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void testPrivateConstructorCoverage() throws Exception {
        java.lang.reflect.Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
    @Test
    void testSecretKeyIsNotEmpty() {
        assertNotNull(Utils.secretKey);
        assertFalse(Utils.secretKey.isEmpty());
    }

    @Test
    void testJwtExpirationTime() {
        assertEquals(60 * 60 * 1000 * 6, Utils.jwtExpirationTime);
    }

    @Test
    void testRequestOkConstant() {
        assertEquals(1, Utils.REQUEST_OK);
    }

    @Test
    void testRequestErrorConstant() {
        assertEquals(0, Utils.REQUEST_ERROR);
    }
}
