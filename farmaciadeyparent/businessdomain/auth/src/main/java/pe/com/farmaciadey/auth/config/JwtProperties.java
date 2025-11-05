package pe.com.farmaciadey.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración para JWT
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    /**
     * Clave secreta para firmar los tokens JWT
     */
    private String secret = "default-secret-key-change-in-production";
    
    /**
     * Tiempo de expiración del token en milisegundos
     */
    private Long expiration = 86400000L; // 24 horas por defecto
    
}