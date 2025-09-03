package pe.com.farmaciadey.auth.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import pe.com.farmaciadey.auth.models.responses.DataExceptionResponse;
import pe.com.farmaciadey.auth.services.JwtService;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String path = request.getRequestURI();
        if (path.startsWith("/auth/v3/api-docs") || path.startsWith("/auth/swagger-ui")
                || path.startsWith("/auth/swagger-resources")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        DataExceptionResponse exceptionResponse = new DataExceptionResponse();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }

            } catch (ExpiredJwtException exception) {
                logger.warn("Request to parse expired JWT. " + exception.getMessage());
                exceptionResponse.setMensaje("El token ha expirado");
            } catch (UnsupportedJwtException exception) {
                logger.warn("Request to parse unsupported JWT ");
                exceptionResponse.setMensaje("Token no admitido.");
            } catch (MalformedJwtException exception) {
                logger.warn("Request to parse invalid JWT. " + exception.getMessage());
                exceptionResponse.setMensaje("Token no válido.");
            } catch (SignatureException exception) {
                logger.warn("Request to parse JWT with invalid signature. " + exception.getMessage());
                exceptionResponse.setMensaje("Token con firma no válida.");
            } catch (IllegalArgumentException exception) {
                logger.warn("Request to parse empty or null JWT." + exception.getMessage());
                exceptionResponse.setMensaje("Token está vacío.");
            } finally {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                exceptionResponse.setEstado(-3);
                request.setAttribute("error", exceptionResponse);
                filterChain.doFilter(request, response);
            }

        } else {
            exceptionResponse.setMensaje("Sin Token");
            exceptionResponse.setEstado(-3);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", exceptionResponse);
            filterChain.doFilter(request, response);
        }

    }
}
