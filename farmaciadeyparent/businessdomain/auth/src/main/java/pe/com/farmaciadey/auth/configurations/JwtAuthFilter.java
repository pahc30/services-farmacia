package pe.com.farmaciadey.auth.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import pe.com.farmaciadey.auth.services.JwtService;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Si tienes prefijos/paths públicos, déjalos pasar directamente
    private boolean isWhitelisted(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/swagger-resources")
            || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        // 1) Rutas públicas (sin verificar token)
        if (isWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Si no hay Authorization Bearer, continúa; el Security config decidirá si requiere auth
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expirado: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "El token ha expirado.");
            return;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT no soportado: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no admitido.");
            return;
        } catch (MalformedJwtException e) {
            logger.warn("JWT inválido: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido.");
            return;
        } catch (SignatureException e) {
            logger.warn("Firma JWT inválida: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Firma de token no válida.");
            return;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT vacío o nulo: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token vacío.");
            return;
        }

        // 3) Si ya hay autenticación en contexto, sigue
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) Valida token y establece autenticación
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            // Token no válido → 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido.");
            return;
        }

        // 5) Continúa cadena
        filterChain.doFilter(request, response);
    }
}
