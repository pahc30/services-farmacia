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
 * CENTRALIZADO: Solo el Gateway maneja Security Headers (Best Practice)
 */
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 🔧 MEJOR PRÁCTICA: Usar beforeCommit para agregar headers en el momento correcto
        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            
            // Solo agregar si no existen (evitar duplicados)
            if (!headers.containsKey("X-Content-Type-Options")) {
                headers.add("X-Content-Type-Options", "nosniff");
            }
            
            if (!headers.containsKey("X-Frame-Options")) {
                headers.add("X-Frame-Options", "DENY");
            }
            
            if (!headers.containsKey("X-XSS-Protection")) {
                headers.add("X-XSS-Protection", "1; mode=block");
            }
            
            if (!headers.containsKey("Content-Security-Policy")) {
                headers.add("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                    "font-src 'self' https://fonts.gstatic.com data:; " +
                    "img-src 'self' data: https:; " +
                    "connect-src 'self' http://localhost:* https://*.onrender.com; " +
                    "frame-ancestors 'none';"
                );
            }
            
            if (!headers.containsKey("Referrer-Policy")) {
                headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
            }
            
            if (!headers.containsKey("Permissions-Policy")) {
                headers.add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            }
            
            // Cache-Control para APIs
            if (exchange.getRequest().getURI().getPath().contains("/api/")) {
                if (!headers.containsKey("Cache-Control")) {
                    headers.add("Cache-Control", "no-store, no-cache, must-revalidate, private");
                }
                if (!headers.containsKey("Pragma")) {
                    headers.add("Pragma", "no-cache");
                }
            }
            
            return Mono.empty();
        });
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Alta prioridad para que se ejecute antes que otros filtros
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
