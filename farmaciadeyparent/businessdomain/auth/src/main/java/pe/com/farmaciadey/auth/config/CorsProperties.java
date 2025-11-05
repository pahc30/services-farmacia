package pe.com.farmaciadey.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuración para CORS
 */
@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {
    
    /**
     * Orígenes permitidos para CORS (cors.allowed-origins)
     */
    private List<String> allowedOrigins = List.of("http://localhost:4200");
    
    /**
     * Métodos HTTP permitidos (cors.allowed-methods)
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    
    /**
     * Headers permitidos (cors.allowed-headers)
     */
    private List<String> allowedHeaders = List.of("*");
    
    /**
     * Headers expuestos (cors.exposed-headers)
     */
    private List<String> exposedHeaders = List.of("*");
    
    /**
     * Permitir credenciales (cors.allow-credentials)
     */
    private Boolean allowCredentials = true;
    
}