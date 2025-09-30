package pe.com.farmaciadey.usuario.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.com.farmaciadey.usuario.models.Usuario;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    void testSaveAndFind() {
        Usuario u = new Usuario();
        u.setIdentificacion("123");
        u.setNombres("Juan");
        u.setApellidos("Perez");
        u.setTelefono("999999999");
        u.setEmail("juan@correo.com");
        u.setDireccion("Calle 1");
        u.setRol("ADMIN");
        u.setUsername("juanp");
        u.setPassword("pass");
        u.setEliminado(0);
        Usuario saved = repository.save(u);
        assertNotNull(saved.getId());
        assertEquals("juanp", repository.findByUsername("juanp").getUsername());
        assertEquals("123", repository.findByIdentificacion("123").getIdentificacion());
        assertEquals(saved.getId(), repository.find(saved.getId()).getId());
    }
}
