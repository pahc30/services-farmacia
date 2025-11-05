package pe.com.farmaciadey.compra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuración para WebFlux CORS
 */
@Component
@ConfigurationProperties(prefix = "spring.webflux.cors")
@Getter
@Setter
public class WebFluxCorsProperties {
    
    /**
     * Orígenes permitidos para WebFlux CORS (spring.webflux.cors.allowed-origins)
     */
    private List<String> allowedOrigins = List.of("http://localhost:4200");
    
    /**
     * Métodos HTTP permitidos (spring.webflux.cors.allowed-methods)
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    
    /**
     * Headers permitidos (spring.webflux.cors.allowed-headers)
     */
    private List<String> allowedHeaders = List.of("*");
    
    /**
     * Headers expuestos (spring.webflux.cors.exposed-headers)
     */
    private List<String> exposedHeaders = List.of("*");
    
    /**
     * Permitir credenciales (spring.webflux.cors.allow-credentials)
     */
    private Boolean allowCredentials = true;
    
}