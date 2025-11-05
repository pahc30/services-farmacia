package pe.com.farmaciadey.auth.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;
import pe.com.farmaciadey.auth.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerBlackBoxTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testRegisterUser_ReturnsJwtResponse() throws Exception {
    // Simula el JSON de registro
    String userJson = "{" +
        "\"username\": \"testuser\"," +
        "\"password\": \"testpass\"," +
        "\"email\": \"test@example.com\"}";

    // Simula el comportamiento del servicio
    Mockito.when(jwtService.GenerateToken(Mockito.anyString())).thenReturn("mocked-jwt-token");

    // Simula el registro de usuario
    pe.com.farmaciadey.auth.models.UserInfo mockUser = new pe.com.farmaciadey.auth.models.UserInfo();
    mockUser.setUsername("testuser");
    mockUser.setEmail("test@example.com");
    Mockito.when(userService.save(Mockito.any(pe.com.farmaciadey.auth.models.UserInfo.class))).thenReturn(mockUser);

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.dato.username").value("testuser")); // Verifica que el campo 'dato.username' esté en la respuesta
    }
}
