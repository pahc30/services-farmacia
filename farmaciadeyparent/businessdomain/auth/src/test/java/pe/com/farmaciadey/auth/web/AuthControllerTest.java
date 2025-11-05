package pe.com.farmaciadey.auth.web;

import pe.com.farmaciadey.auth.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pe.com.farmaciadey.auth.controllers.AuthController;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;

@WebMvcTest(controllers = AuthController.class)
@Import(pe.com.farmaciadey.auth.configurations.TestSecurityConfig.class)
class AuthControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private AuthenticationManager authenticationManager;
  @MockitoBean private CustomUserDetailsService userService;
  @MockitoBean private JwtService jwtService;

  @Test
  void login_credencialesValidas_retornaToken_200() throws Exception {
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(auth);
    when(auth.isAuthenticated()).thenReturn(true);
    when(userService.findByUsername("pablo")).thenReturn(new pe.com.farmaciadey.auth.models.UserInfo());
    when(jwtService.GenerateToken("pablo")).thenReturn("jwt.fake.token");

  mvc.perform(post("/api/auth/login")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"username\":\"pablo\",\"password\":\"123456\"}"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void login_credencialesInvalidas_401() throws Exception {
    when(authenticationManager.authenticate(any())).thenThrow(new IllegalArgumentException("Credenciales inválidas"));
  mvc.perform(post("/api/auth/login")
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\"username\":\"pablo\",\"password\":\"bad\"}"))
      .andExpect(status().isUnauthorized());
  }
}
