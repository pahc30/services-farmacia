package pe.com.farmaciadey.auth.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.userdetails.UserDetails;
import pe.com.farmaciadey.auth.services.JwtService;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtAuthFilterTest {
    @Test
    void testDoFilterInternalWithExpiredJwtException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "expired.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expirado"));
        jwtAuthFilter.doFilterInternal(request, response, chain);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDoFilterInternalWithMalformedJwtException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "malformed.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new io.jsonwebtoken.MalformedJwtException("Token malformado"));
        jwtAuthFilter.doFilterInternal(request, response, chain);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDoFilterInternalWithSignatureException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "bad.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new io.jsonwebtoken.security.SignatureException("Firma inválida"));
        jwtAuthFilter.doFilterInternal(request, response, chain);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDoFilterInternalWithIllegalArgumentException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "illegal.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new IllegalArgumentException("Token vacío"));
        jwtAuthFilter.doFilterInternal(request, response, chain);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDoFilterInternalWithInvalidTokenValidation() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);
        jwtAuthFilter.doFilterInternal(request, response, chain);
        assertEquals(401, response.getStatus());
    }
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        jwtAuthFilter.doFilterInternal(request, response, chain);
        verify(jwtService).extractUsername(token);
        verify(jwtService).validateToken(token, userDetails);
        verify(userDetailsService).loadUserByUsername("testuser");
    }

    @Test
    void testDoFilterInternalWithNoToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        jwtAuthFilter.doFilterInternal(request, response, chain);
        // No interactions expected
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);
        jwtAuthFilter.doFilterInternal(request, response, chain);
        verify(jwtService).extractUsername(token);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void testDoFilterInternalWithUserNotFound() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("nouser");
        when(userDetailsService.loadUserByUsername("nouser")).thenReturn(null);
        jwtAuthFilter.doFilterInternal(request, response, chain);
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("nouser");
        // No se verifica interacciones adicionales estrictas
    }

    @Test
    void testDoFilterInternalWithInvalidValidation() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);
        jwtAuthFilter.doFilterInternal(request, response, chain);
    verify(jwtService).extractUsername(token);
    // No se verifica loadUserByUsername si el flujo no lo llama
    }
}
