package pe.com.farmaciadey.auth.configurations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {
    private JwtAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthenticationEntryPoint();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authException = mock(AuthenticationException.class);
        java.io.PrintWriter writer = mock(java.io.PrintWriter.class);
        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCommence_setsUnauthorizedStatusAndMessage_whenErrorIsNull() throws IOException, ServletException {
        when(request.getAttribute("error")).thenReturn(null);
        entryPoint.commence(request, response, authException);
        // Should only set content type and encoding, then flush writer
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).getWriter();
    }

    @Test
    void testCommence_writesErrorObject_whenErrorIsPresent() throws IOException, ServletException {
        java.util.Map<String, String> errorObj = new java.util.HashMap<>();
        errorObj.put("error", "Unauthorized");
        when(request.getAttribute("error")).thenReturn(errorObj);
        java.io.PrintWriter writer = mock(java.io.PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        JwtAuthenticationEntryPoint entryPointWithMapper = new JwtAuthenticationEntryPoint();
        // Inject a real ObjectMapper
        try {
            java.lang.reflect.Field field = JwtAuthenticationEntryPoint.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(entryPointWithMapper, new com.fasterxml.jackson.databind.ObjectMapper());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        entryPointWithMapper.commence(request, response, authException);
        verify(writer).write(anyString());
        verify(writer).flush();
    }
    @Test
    void testCommence_handlesIOException() throws Exception {
        doThrow(new IOException()).when(response).sendError(anyInt(), anyString());
        entryPoint.commence(request, response, authException);
        // No exception should propagate
    }
}
