package pe.com.farmaciadey.metodopago.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración para el sistema de pagos
 */
@Component
@ConfigurationProperties(prefix = "payment")
@Getter
@Setter
public class PaymentProperties {
    
    /**
     * Proveedor de pagos a utilizar (simulado, stripe, mercadopago)
     */
    private String provider = "simulado";
    
}