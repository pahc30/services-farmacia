package pe.com.farmaciadey.usuario.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    @Test
    void testGettersAndSetters() {
        Usuario u = new Usuario();
        u.setId(1);
        u.setIdentificacion("123");
        u.setNombres("Juan");
        u.setApellidos("Perez");
        u.setTelefono("999999999");
        u.setEmail("juan@correo.com");
        u.setDireccion("Calle 1");
        u.setRol("ADMIN");
        u.setUsername("juanp");
        u.setPassword("pass");
        u.setEliminado(1);
        assertEquals(1, u.getId());
        assertEquals("123", u.getIdentificacion());
        assertEquals("Juan", u.getNombres());
        assertEquals("Perez", u.getApellidos());
        assertEquals("999999999", u.getTelefono());
        assertEquals("juan@correo.com", u.getEmail());
        assertEquals("Calle 1", u.getDireccion());
        assertEquals("ADMIN", u.getRol());
        assertEquals("juanp", u.getUsername());
        assertEquals("pass", u.getPassword());
        assertEquals(1, u.getEliminado());
    }
}
