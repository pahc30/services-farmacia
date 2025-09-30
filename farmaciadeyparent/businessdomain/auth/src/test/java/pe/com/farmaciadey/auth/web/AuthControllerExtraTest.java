package pe.com.farmaciadey.auth.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.com.farmaciadey.auth.controllers.AuthController;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;
import pe.com.farmaciadey.auth.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(pe.com.farmaciadey.auth.configurations.TestSecurityConfig.class)
@WebMvcTest(controllers = AuthController.class)
class AuthControllerExtraTest {
    @Autowired private MockMvc mvc;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private CustomUserDetailsService userService;
    @MockBean private JwtService jwtService;

    @Test
    void usuarioRegistrar_usernameExistente_retornaError() throws Exception {
        UserInfo user = new UserInfo();
        user.setUsername("pablo");
        when(userService.findByUsername("pablo")).thenReturn(user);
        mvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"pablo\",\"password\":\"123456\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.mensaje").value("El username pablo ya existe"));
    }

    @Test
    void usuarioRegistrar_nuevoUsuario_retornaOk() throws Exception {
        UserInfo user = new UserInfo();
        user.setUsername("nuevo");
        when(userService.findByUsername("nuevo")).thenReturn(null);
        when(userService.save(any())).thenReturn(user);
        mvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"nuevo\",\"password\":\"123456\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dato.username").value("nuevo"));
    }

    @Test
    void usuarioRegistrar_errorInterno_retornaError() throws Exception {
        when(userService.findByUsername("error")).thenReturn(null);
        when(userService.save(any())).thenThrow(new RuntimeException("DB error"));
        mvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"error\",\"password\":\"123456\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Error interno.")));
    }

        @Test
        void usuarioRegistrar_saveRetornaNull_retornaError() throws Exception {
            when(userService.findByUsername("nulluser")).thenReturn(null);
            when(userService.save(any())).thenReturn(null);
            mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nulluser\",\"password\":\"123456\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("No se pudo registrar el usuario"));
        }

        @Test
        void login_usuarioNull_retorna401() throws Exception {
            org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(auth.isAuthenticated()).thenReturn(true);
            when(userService.findByUsername("nouser")).thenReturn(null);
            mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nouser\",\"password\":\"123456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
        }

        @Test
        void login_noAutenticado_retorna401() throws Exception {
            org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(auth.isAuthenticated()).thenReturn(false);
            mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"noauth\",\"password\":\"123456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
        }
}
