package pe.com.farmaciadey.auth.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {
    @Test
    void testConstructorAndGetters() {
        UserInfo user = new UserInfo();
        user.setUsername("testuser");
        user.setPassword("testpass");
        CustomUserDetails details = new CustomUserDetails(user);
        assertEquals("testuser", details.getUsername());
        assertEquals("testpass", details.getPassword());
        assertNotNull(details.getAuthorities());
        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void testAccountNonExpired() {
        UserInfo user = new UserInfo();
        CustomUserDetails details = new CustomUserDetails(user);
        assertTrue(details.isAccountNonExpired());
    }

    @Test
    void testAccountNonLocked() {
        UserInfo user = new UserInfo();
        CustomUserDetails details = new CustomUserDetails(user);
        assertTrue(details.isAccountNonLocked());
    }

    @Test
    void testCredentialsNonExpired() {
        UserInfo user = new UserInfo();
        CustomUserDetails details = new CustomUserDetails(user);
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        UserInfo user = new UserInfo();
        CustomUserDetails details = new CustomUserDetails(user);
        assertTrue(details.isEnabled());
    }
}
