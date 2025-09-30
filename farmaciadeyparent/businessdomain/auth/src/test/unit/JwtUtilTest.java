package pe.com.farmaciadey.auth.unit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pe.com.farmaciadey.auth.security.JwtUtil; // ajusta package si difiere

class JwtUtilTest {

  // Ajusta el constructor a tu implementación (secret y expiración)
  private final JwtUtil jwt = new JwtUtil("secretDePruebaSuperSeguro", 3600);

  @Test
  void generaYValidaToken_ok() {
    String token = jwt.generateToken("pablo");
    assertNotNull(token);

    String user = jwt.getUsernameFromToken(token);
    assertEquals("pablo", user);

    assertTrue(jwt.validateToken(token, "pablo"));
  }

  @Test
  void validaToken_usuarioIncorrecto_falla() {
    String token = jwt.generateToken("pablo");
    assertFalse(jwt.validateToken(token, "otro"));
  }
}
