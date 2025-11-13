package pe.com.farmaciadey.usuario.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.List;

/**
 * Configuración de seguridad para el microservicio de Usuario
 * Implementa CORS y Security Headers según OWASP Best Practices
 */
@Configuration
public class SecurityConfig {

    /**
     * Filtro CORS global
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:*", 
            "http://127.0.0.1:*", 
            "https://*.onrender.com",
            "https://*.vercel.app",
            "https://*.netlify.app"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * Filtro para agregar Security Headers a todas las respuestas
     * Implementa OWASP Security Headers Best Practices
     */
    // 🏗️ ARQUITECTURA LIMPIA: Security Headers manejados ÚNICAMENTE por API Gateway
    // Microservicio enfocado en lógica de negocio, no en headers HTTP
    // @Bean
    // public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
    //     FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new SecurityHeadersFilter());
    //     registrationBean.addUrlPatterns("/*");
    //     registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    //     return registrationBean;
    // }

    /**
     * Filtro personalizado para Security Headers
     */
    private static class SecurityHeadersFilter implements Filter {
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // 🔒 X-Content-Type-Options: Prevenir MIME sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            
            // 🔒 X-Frame-Options: Prevenir clickjacking
            httpResponse.setHeader("X-Frame-Options", "DENY");
            
            // 🔒 X-XSS-Protection: Protección XSS del navegador
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            
            // 🔒 Content-Security-Policy: Política de seguridad de contenido
            httpResponse.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "connect-src 'self' http://localhost:* https://*.onrender.com; " +
                "frame-ancestors 'none';"
            );
            
            // 🔒 Referrer-Policy: Control de información de referencia
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // 🔒 Permissions-Policy: Control de características del navegador
            httpResponse.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            
            // 🔒 Cache-Control para endpoints de API
            String path = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
            if (path.contains("/api/")) {
                httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
                httpResponse.setHeader("Pragma", "no-cache");
            }
            
            chain.doFilter(request, response);
        }
    }
}
