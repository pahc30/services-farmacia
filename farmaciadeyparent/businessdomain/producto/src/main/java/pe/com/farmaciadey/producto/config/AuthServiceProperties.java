package pe.com.farmaciadey.producto.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración para propiedades de servicios de autenticación
 */
@Component
@ConfigurationProperties(prefix = "auth.service")
@Getter
@Setter
public class AuthServiceProperties {
    
    /**
     * URL del servicio de autenticación
     */
    private String url = "http://localhost:8081";
    
}