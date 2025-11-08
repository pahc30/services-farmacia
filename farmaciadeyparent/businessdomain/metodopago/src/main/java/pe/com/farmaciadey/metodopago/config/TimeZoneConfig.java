package pe.com.farmaciadey.metodopago.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {
    
    @PostConstruct
    public void init() {
        // Configurar la zona horaria por defecto de la aplicación a Peru (UTC-5)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }
}