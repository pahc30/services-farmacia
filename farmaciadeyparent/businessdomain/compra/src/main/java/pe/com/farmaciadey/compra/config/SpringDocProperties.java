package pe.com.farmaciadey.compra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración para SpringDoc OpenAPI
 */
@Component
@ConfigurationProperties(prefix = "springdoc.swagger-ui")
@Getter
@Setter
public class SpringDocProperties {
    
    /**
     * Ruta de Swagger UI
     */
    private String path = "/swagger-ui.html";
    
    /**
     * Habilitar Swagger UI
     */
    private Boolean enabled = true;
    
}