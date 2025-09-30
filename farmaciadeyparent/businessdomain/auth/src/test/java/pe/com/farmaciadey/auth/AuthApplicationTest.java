package pe.com.farmaciadey.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthApplicationTest {
    @Test
    void contextLoads() {
        // If the context fails to load, this test will fail
        assertTrue(true);
    }

    @Test
    void mainRunsWithoutException() {
        assertDoesNotThrow(() -> AuthApplication.main(new String[]{}));
    }
}
