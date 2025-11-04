package pe.com.farmaciadey.metodopago.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.farmaciadey.metodopago.models.TransaccionPago;
import pe.com.farmaciadey.metodopago.models.requests.PaymentIntentRequest;
import pe.com.farmaciadey.metodopago.models.responses.PaymentIntentResponse;
import pe.com.farmaciadey.metodopago.repository.TransaccionPagoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de pagos simulados para desarrollo y demos
 * NO usar en producción real con dinero
 * Sistema completamente GRATUITO para pruebas
 */
@Slf4j
@Service
public class SimulatedPaymentService {

    private final Random random = new Random();
    
    @Autowired
    private TransaccionPagoRepository transaccionRepository;
    
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) {
        log.info("🎭 Creando pago SIMULADO para compra: {}, monto: {}", 
                request.getCompraId(), request.getMonto());
        
        try {
            // Crear transacción en la base de datos
            TransaccionPago transaccion = new TransaccionPago();
            transaccion.setCompraId(request.getCompraId());
            transaccion.setMetodoPagoId(2L); // Default Yape/Plin (ID=2)
            transaccion.setMonto(BigDecimal.valueOf(request.getMonto()));
            transaccion.setMoneda("PEN");
            transaccion.setEstado(TransaccionPago.EstadoTransaccion.PENDIENTE);
            transaccion.setFechaCreacion(LocalDateTime.now());
            transaccion.setFechaActualizacion(LocalDateTime.now());
            transaccion.setDescripcion("Pago simulado - " + request.getDescripcion());
            transaccion.setEliminado(0);
            
            // Generar referencias simuladas
            String clientSecret = "pi_" + System.currentTimeMillis() + "_secret_sim";
            transaccion.setClientSecret(clientSecret);
            transaccion.setReferenciaExterna("sim_" + System.currentTimeMillis());
            
            // Guardar en base de datos
            transaccion = transaccionRepository.save(transaccion);
            
            log.info("✅ Transacción simulada creada exitosamente: ID={}", transaccion.getId());
            
            return PaymentIntentResponse.builder()
                    .success(true)
                    .transaccionId(transaccion.getId())
                    .clientSecret(clientSecret)
                    .stripePaymentIntentId(transaccion.getReferenciaExterna())
                    .message("Pago simulado creado exitosamente")
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Error creando transacción simulada: {}", e.getMessage(), e);
            return PaymentIntentResponse.builder()
                    .success(false)
                    .transaccionId(null)
                    .clientSecret(null)
                    .stripePaymentIntentId(null)
                    .message("Error creando pago simulado: " + e.getMessage())
                    .error("SIMULATION_ERROR")
                    .build();
        }
    }
    
    public boolean confirmarPago(String paymentIntentId) {
        log.info("🎭 Confirmando pago SIMULADO por referencia: {}", paymentIntentId);
        
        try {
            // Buscar por referencia externa
            Optional<TransaccionPago> transaccionOpt = transaccionRepository.findByReferenciaExterna(paymentIntentId);
            if (transaccionOpt.isEmpty()) {
                log.warn("❌ No se encontró transacción con referencia: {}", paymentIntentId);
                return false;
            }
            
            TransaccionPago transaccion = transaccionOpt.get();
            
            // Simular confirmación (90% de éxito)
            boolean confirmed = random.nextDouble() > 0.1;
            
            if (confirmed) {
                transaccion.setEstado(TransaccionPago.EstadoTransaccion.COMPLETADA);
                transaccion.setFechaPago(LocalDateTime.now());
                transaccion.setFechaActualizacion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                log.info("✅ Pago confirmado exitosamente: ID={}", transaccion.getId());
                return true;
            } else {
                transaccion.setEstado(TransaccionPago.EstadoTransaccion.FALLIDA);
                transaccion.setMensajeError("Simulación de fallo en confirmación");
                transaccion.setFechaActualizacion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                log.warn("❌ Confirmación simulada fallida: ID={}", transaccion.getId());
                return false;
            }
            
        } catch (Exception e) {
            log.error("❌ Error confirmando pago: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Sobrecarga para aceptar Long ID
     */
    public boolean confirmarPago(Long transaccionId) {
        log.info("🎭 Confirmando pago SIMULADO por ID: {}", transaccionId);
        
        try {
            Optional<TransaccionPago> transaccionOpt = transaccionRepository.findById(transaccionId);
            if (transaccionOpt.isEmpty()) {
                log.warn("❌ No se encontró transacción con ID: {}", transaccionId);
                return false;
            }
            
            TransaccionPago transaccion = transaccionOpt.get();
            
            // Simular confirmación (90% de éxito)
            boolean confirmed = random.nextDouble() > 0.1;
            
            if (confirmed) {
                transaccion.setEstado(TransaccionPago.EstadoTransaccion.COMPLETADA);
                transaccion.setFechaPago(LocalDateTime.now());
                transaccion.setFechaActualizacion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                log.info("✅ Pago confirmado exitosamente: ID={}", transaccion.getId());
                return true;
            } else {
                transaccion.setEstado(TransaccionPago.EstadoTransaccion.FALLIDA);
                transaccion.setMensajeError("Simulación de fallo en confirmación");
                transaccion.setFechaActualizacion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                log.warn("❌ Confirmación simulada fallida: ID={}", transaccion.getId());
                return false;
            }
            
        } catch (Exception e) {
            log.error("❌ Error confirmando pago: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public Map<String, Object> obtenerEstadoPago(Long transaccionId) {
        log.info("🔍 Consultando estado de transacción: {}", transaccionId);
        
        try {
            Optional<TransaccionPago> transaccionOpt = transaccionRepository.findById(transaccionId);
            if (transaccionOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Transacción no encontrada");
                errorResponse.put("transaccionId", transaccionId);
                return errorResponse;
            }
            
            TransaccionPago transaccion = transaccionOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("transaccionId", transaccion.getId());
            response.put("compraId", transaccion.getCompraId());
            response.put("metodoPagoId", transaccion.getMetodoPagoId());
            response.put("monto", transaccion.getMonto());
            response.put("moneda", transaccion.getMoneda());
            response.put("estado", transaccion.getEstado().toString());
            response.put("fechaCreacion", transaccion.getFechaCreacion());
            response.put("fechaPago", transaccion.getFechaPago());
            response.put("descripcion", transaccion.getDescripcion());
            response.put("referenciaExterna", transaccion.getReferenciaExterna());
            response.put("mensajeError", transaccion.getMensajeError());
            response.put("provider", "SIMULADO");
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo estado de transacción: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno: " + e.getMessage());
            errorResponse.put("transaccionId", transaccionId);
            return errorResponse;
        }
    }
    
    public List<Map<String, Object>> obtenerTransaccionesPorCompra(Long compraId) {
        log.info("🔍 Consultando transacciones para compra: {}", compraId);
        
        try {
            List<TransaccionPago> transacciones = transaccionRepository.findByCompraId(compraId);
            
            List<Map<String, Object>> response = new ArrayList<>();
            
            for (TransaccionPago transaccion : transacciones) {
                Map<String, Object> item = new HashMap<>();
                item.put("transaccionId", transaccion.getId());
                item.put("compraId", transaccion.getCompraId());
                item.put("metodoPagoId", transaccion.getMetodoPagoId());
                item.put("monto", transaccion.getMonto());
                item.put("moneda", transaccion.getMoneda());
                item.put("estado", transaccion.getEstado().toString());
                item.put("fechaCreacion", transaccion.getFechaCreacion());
                item.put("fechaPago", transaccion.getFechaPago());
                item.put("descripcion", transaccion.getDescripcion());
                item.put("referenciaExterna", transaccion.getReferenciaExterna());
                item.put("mensajeError", transaccion.getMensajeError());
                response.add(item);
            }
            
            log.info("✅ Encontradas {} transacciones para compra: {}", response.size(), compraId);
            return response;
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo transacciones por compra: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public boolean isAvailable() {
        return true; // El servicio simulado siempre está disponible
    }
    
    public Map<String, Object> simularError(String tipoError) {
        Map<String, Object> response = new HashMap<>();
        
        switch (tipoError.toLowerCase()) {
            case "tarjeta_rechazada":
                response.put("error", "CARD_DECLINED");
                response.put("message", "Su tarjeta fue rechazada por el banco");
                break;
            case "fondos_insuficientes":
                response.put("error", "INSUFFICIENT_FUNDS");
                response.put("message", "Fondos insuficientes en su cuenta");
                break;
            case "conexion_timeout":
                response.put("error", "TIMEOUT");
                response.put("message", "Tiempo de espera agotado");
                break;
            default:
                response.put("error", "UNKNOWN_ERROR");
                response.put("message", "Error desconocido en el sistema de pagos");
        }
        
        response.put("tipoError", tipoError);
        response.put("timestamp", System.currentTimeMillis());
        response.put("provider", "SIMULADO");
        
        return response;
    }
}