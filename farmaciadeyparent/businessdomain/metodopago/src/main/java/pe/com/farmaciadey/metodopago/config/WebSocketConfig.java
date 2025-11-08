package pe.com.farmaciadey.metodopago.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para notificaciones en tiempo real
 * Utilizado para detectar cuando se escanea un QR de pago
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar un simple broker de mensajes in-memory
        config.enableSimpleBroker("/topic");
        
        // Prefijo para destinos de aplicación
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conectarse vía WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // En producción, especificar dominios exactos
                .withSockJS();  // Fallback para navegadores que no soporten WebSocket nativo
    }
}