package pe.com.farmaciadey.auth.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock UserRepository repository;
    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadUserByUsername_usuarioExiste_retornaUserDetails() {
        UserInfo user = new UserInfo();
        user.setUsername("pablo");
        when(repository.findByUsername("pablo")).thenReturn(user);
        UserDetails details = service.loadUserByUsername("pablo");
        assertNotNull(details);
        assertEquals("pablo", details.getUsername());
    }

    @Test
    void loadUserByUsername_usuarioNoExiste_lanzaExcepcion() {
        when(repository.findByUsername("noexiste")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("noexiste"));
    }

    @Test
    void findByUsername_devuelveUsuario() {
        UserInfo user = new UserInfo();
        user.setUsername("pablo");
        when(repository.findByUsername("pablo")).thenReturn(user);
        UserInfo result = service.findByUsername("pablo");
        assertEquals("pablo", result.getUsername());
    }

    @Test
    void save_guardaUsuario() {
    UserInfo user = new UserInfo();
    user.setUsername("nuevo");
    user.setPassword("123456");
    when(repository.save(any())).thenReturn(user);
    UserInfo result = service.save(user);
    assertEquals("nuevo", result.getUsername());
    }
}
