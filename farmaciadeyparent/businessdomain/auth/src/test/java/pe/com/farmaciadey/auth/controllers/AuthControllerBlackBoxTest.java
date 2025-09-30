package pe.com.farmaciadey.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mockito;
import pe.com.farmaciadey.auth.models.responses.JwtResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private CustomUserDetailsService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser_ReturnsJwtResponse() throws Exception {
    // Simula el JSON de registro
    String userJson = "{" +
        "\"username\": \"testuser\"," +
        "\"password\": \"testpass\"," +
        "\"email\": \"test@example.com\"}";

    // Simula el comportamiento del servicio
    JwtResponse jwtResponse = JwtResponse.builder()
        .accessToken("mocked-jwt-token")
        .user(null)
        .build();
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
