package pe.com.farmaciadey.appgw.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para agregar Security Headers a todas las respuestas del API Gateway
 * Implementa las mejores prácticas de OWASP para cabeceras HTTP de seguridad
 */
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            
            // 🔒 X-Content-Type-Options: Prevenir MIME sniffing
            headers.add("X-Content-Type-Options", "nosniff");
            
            // 🔒 X-Frame-Options: Prevenir clickjacking
            headers.add("X-Frame-Options", "DENY");
            
            // 🔒 X-XSS-Protection: Protección XSS del navegador (legacy browsers)
            headers.add("X-XSS-Protection", "1; mode=block");
            
            // 🔒 Strict-Transport-Security: Forzar HTTPS (solo en producción con HTTPS)
            // Comentado para desarrollo local, descomentar en producción
            // headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            
            // 🔒 Content-Security-Policy: Política de seguridad de contenido
            headers.add("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                "font-src 'self' https://fonts.gstatic.com data:; " +
                "img-src 'self' data: https:; " +
                "connect-src 'self' http://localhost:* https://*.onrender.com; " +
                "frame-ancestors 'none';"
            );
            
            // 🔒 Referrer-Policy: Control de información de referencia
            headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // 🔒 Permissions-Policy: Control de características del navegador
            headers.add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            
            // 🔒 Cache-Control: Prevenir almacenamiento en caché de datos sensibles
            if (exchange.getRequest().getURI().getPath().contains("/api/")) {
                headers.add("Cache-Control", "no-store, no-cache, must-revalidate, private");
                headers.add("Pragma", "no-cache");
            }
        }));
    }

    @Override
    public int getOrder() {
        // Alta prioridad para que se ejecute antes que otros filtros
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
