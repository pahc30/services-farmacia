package pe.com.farmaciadey.compra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompraApplicationTest {
    @Test
    void testMainRunsWithoutException() {
        assertDoesNotThrow(() -> CompraApplication.main(new String[]{}));
    }
}
