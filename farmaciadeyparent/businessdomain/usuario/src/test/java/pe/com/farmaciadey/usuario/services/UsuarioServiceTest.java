package pe.com.farmaciadey.usuario.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import pe.com.farmaciadey.usuario.models.Usuario;
import pe.com.farmaciadey.usuario.repository.UsuarioRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
        Usuario u = new Usuario();
        when(repository.list()).thenReturn(Arrays.asList(u));
        assertEquals(1, service.list().size());
    }

    @Test
    void testSaveSuccess() throws Exception {
        Usuario u = new Usuario();
        u.setUsername("user1");
        u.setIdentificacion("123");
        u.setPassword("pass");
        when(repository.findByUsername("user1")).thenReturn(null);
        when(repository.findByIdentificacion("123")).thenReturn(null);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        Usuario saved = service.save(u);
        assertNotNull(saved.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("pass", saved.getPassword()));
    }

    @Test
    void testSaveUsernameExists() {
        Usuario u = new Usuario();
        u.setUsername("user1");
        u.setIdentificacion("123");
        u.setPassword("pass");
        when(repository.findByUsername("user1")).thenReturn(new Usuario());
        Exception ex = assertThrows(Exception.class, () -> service.save(u));
        assertTrue(ex.getMessage().contains("ya existe"));
    }

    @Test
    void testSaveIdentificacionExists() {
        Usuario u = new Usuario();
        u.setUsername("user1");
        u.setIdentificacion("123");
        u.setPassword("pass");
        when(repository.findByUsername("user1")).thenReturn(null);
        when(repository.findByIdentificacion("123")).thenReturn(new Usuario());
        Exception ex = assertThrows(Exception.class, () -> service.save(u));
        assertTrue(ex.getMessage().contains("ya existe"));
    }

    @Test
    void testUpdateSuccess() {
        Usuario u = new Usuario();
        u.setId(1);
        u.setPassword("newpass");
        Optional<Usuario> opt = Optional.of(new Usuario());
        when(repository.findById(1)).thenReturn(opt);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        Usuario updated = service.update(u);
        assertNotNull(updated);
        assertTrue(new BCryptPasswordEncoder().matches("newpass", updated.getPassword()));
    }

    @Test
    void testUpdateNotFound() {
        Usuario u = new Usuario();
        u.setId(99);
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertNull(service.update(u));
    }

    @Test
    void testDeleteSuccess() {
        Usuario u = new Usuario();
        u.setId(1);
        Optional<Usuario> opt = Optional.of(u);
        when(repository.findById(1)).thenReturn(opt);
        when(repository.save(any(Usuario.class))).thenReturn(u);
        assertTrue(service.delete(1));
    }

    @Test
    void testDeleteNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertFalse(service.delete(99));
    }

    @Test
    void testFind() {
        Usuario u = new Usuario();
        when(repository.find(1)).thenReturn(u);
        assertEquals(u, service.find(1));
    }
}
