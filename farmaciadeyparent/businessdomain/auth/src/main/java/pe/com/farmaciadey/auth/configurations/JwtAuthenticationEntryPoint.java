package pe.com.farmaciadey.auth.configurations;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		Object obj = request.getAttribute("error");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if (obj != null) {
			response.getWriter().write(objectMapper.writeValueAsString(obj));
		}

		response.getWriter().flush();

	}
}
