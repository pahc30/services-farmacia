package pe.com.farmaciadey.producto.config;

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
 * Configuración de seguridad para el microservicio de Producto
 * Implementa CORS y Security Headers según OWASP Best Practices
 */
@Configuration
public class SecurityConfig {

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

    private static class SecurityHeadersFilter implements Filter {
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // 🔒 Security Headers - OWASP Best Practices
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "connect-src 'self' http://localhost:* https://*.onrender.com; " +
                "frame-ancestors 'none';"
            );
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResponse.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            
            String path = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
            if (path.contains("/api/")) {
                httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
                httpResponse.setHeader("Pragma", "no-cache");
            }
            
            chain.doFilter(request, response);
        }
    }
}
