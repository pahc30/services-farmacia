package pe.com.farmaciadey.metodopago.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.farmaciadey.metodopago.models.TransaccionPago;
import pe.com.farmaciadey.metodopago.repository.TransaccionPagoRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para detectar cuando se escanea un QR de pago
 * Maneja la lógica de detección y notificación en tiempo real
 */
@Slf4j
@Service
public class QRScanDetectionService {

    @Autowired
    private TransaccionPagoRepository transaccionRepository;

    // Cache para rastrear QRs pendientes de ser escaneados
    private final Map<String, QRScanSession> qrSessions = new ConcurrentHashMap<>();

    /**
     * Genera un QR de pago y lo registra para detección
     */
    public QRPaymentData generarQRPago(Long transaccionId) {
        try {
            Optional<TransaccionPago> transaccionOpt = transaccionRepository.findById(transaccionId);
            if (transaccionOpt.isEmpty()) {
                throw new RuntimeException("Transacción no encontrada: " + transaccionId);
            }

            TransaccionPago transaccion = transaccionOpt.get();
            
            // Generar QR único
            String qrToken = "QR_" + System.currentTimeMillis() + "_" + transaccionId;
            
            // Crear URL que contendrá los datos del pago
            String qrUrl = generarUrlQR(qrToken, transaccion);
            
            // Registrar sesión de QR
            QRScanSession session = new QRScanSession();
            session.setTransaccionId(transaccionId);
            session.setQrToken(qrToken);
            session.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
            session.setEscaneado(false);
            
            qrSessions.put(qrToken, session);
            
            log.info("🔲 QR generado para transacción {}: {}", transaccionId, qrToken);
            
            return QRPaymentData.builder()
                    .qrToken(qrToken)
                    .qrUrl(qrUrl)
                    .transaccionId(transaccionId)
                    .monto(transaccion.getMonto())
                    .descripcion(transaccion.getDescripcion())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Error generando QR para transacción {}: {}", transaccionId, e.getMessage());
            throw new RuntimeException("Error generando QR de pago", e);
        }
    }

    /**
     * Endpoint que se llama cuando alguien escanea el QR
     */
    public boolean procesarEscaneoQR(String qrToken) {
        try {
            QRScanSession session = qrSessions.get(qrToken);
            if (session == null) {
                log.warn("⚠️ QR token no válido o expirado: {}", qrToken);
                return false;
            }

            if (session.isEscaneado()) {
                log.warn("⚠️ QR ya fue escaneado anteriormente: {}", qrToken);
                return false;
            }

            // Marcar como escaneado
            session.setEscaneado(true);
            session.setFechaEscaneo(LocalDateTime.now(ZoneId.of("America/Lima")));

            log.info("📱 QR escaneado detectado: {} para transacción: {}", 
                    qrToken, session.getTransaccionId());

            // Notificar a través de WebSocket
            notificarEscaneoQR(session);

            // Opcional: Auto-confirmar el pago después del escaneo
            // confirmarPagoAutomaticamente(session.getTransaccionId());

            return true;

        } catch (Exception e) {
            log.error("❌ Error procesando escaneo QR {}: {}", qrToken, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un QR ha sido escaneado
     */
    public boolean verificarEscaneoQR(String qrToken) {
        QRScanSession session = qrSessions.get(qrToken);
        return session != null && session.isEscaneado();
    }

    /**
     * Obtiene el estado de una sesión QR
     */
    public Map<String, Object> obtenerEstadoQR(String qrToken) {
        Map<String, Object> estado = new HashMap<>();
        QRScanSession session = qrSessions.get(qrToken);
        
        if (session == null) {
            estado.put("valido", false);
            estado.put("mensaje", "QR no válido o expirado");
            return estado;
        }

        estado.put("valido", true);
        estado.put("escaneado", session.isEscaneado());
        estado.put("transaccionId", session.getTransaccionId());
        estado.put("fechaCreacion", session.getFechaCreacion());
        
        if (session.isEscaneado()) {
            estado.put("fechaEscaneo", session.getFechaEscaneo());
        }

        return estado;
    }

    private String generarUrlQR(String qrToken, TransaccionPago transaccion) {
        // URL que se almacenará en el QR
        // Cuando escanees esto, te llevará a una página que registra el escaneo
        return String.format("http://localhost:7014/metodopago/api/v1/pagos/qr/scan/%s?t=%s&amt=%.2f", 
                qrToken, 
                System.currentTimeMillis(),
                transaccion.getMonto());
    }

    private void notificarEscaneoQR(QRScanSession session) {
        try {
            // Por ahora, solo loggeamos la notificación
            // En futuras versiones se puede implementar WebSocket
            log.info("📡 QR escaneado - Transacción: {}, Token: {}", 
                    session.getTransaccionId(), session.getQrToken());
            
            // Aquí se podría integrar con:
            // - WebSocket (cuando esté configurado)
            // - Push notifications
            // - Email notifications
            // - SMS notifications

        } catch (Exception e) {
            log.error("❌ Error notificando escaneo QR: {}", e.getMessage());
        }
    }

    // Clases internas para manejo de datos
    public static class QRScanSession {
        private Long transaccionId;
        private String qrToken;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaEscaneo;
        private boolean escaneado;

        // Getters y Setters
        public Long getTransaccionId() { return transaccionId; }
        public void setTransaccionId(Long transaccionId) { this.transaccionId = transaccionId; }
        
        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }
        
        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
        
        public LocalDateTime getFechaEscaneo() { return fechaEscaneo; }
        public void setFechaEscaneo(LocalDateTime fechaEscaneo) { this.fechaEscaneo = fechaEscaneo; }
        
        public boolean isEscaneado() { return escaneado; }
        public void setEscaneado(boolean escaneado) { this.escaneado = escaneado; }
    }

    public static class QRPaymentData {
        private String qrToken;
        private String qrUrl;
        private Long transaccionId;
        private java.math.BigDecimal monto;
        private String descripcion;

        public static QRPaymentDataBuilder builder() {
            return new QRPaymentDataBuilder();
        }

        // Getters
        public String getQrToken() { return qrToken; }
        public String getQrUrl() { return qrUrl; }
        public Long getTransaccionId() { return transaccionId; }
        public java.math.BigDecimal getMonto() { return monto; }
        public String getDescripcion() { return descripcion; }

        public static class QRPaymentDataBuilder {
            private String qrToken;
            private String qrUrl;
            private Long transaccionId;
            private java.math.BigDecimal monto;
            private String descripcion;

            public QRPaymentDataBuilder qrToken(String qrToken) { this.qrToken = qrToken; return this; }
            public QRPaymentDataBuilder qrUrl(String qrUrl) { this.qrUrl = qrUrl; return this; }
            public QRPaymentDataBuilder transaccionId(Long transaccionId) { this.transaccionId = transaccionId; return this; }
            public QRPaymentDataBuilder monto(java.math.BigDecimal monto) { this.monto = monto; return this; }
            public QRPaymentDataBuilder descripcion(String descripcion) { this.descripcion = descripcion; return this; }

            public QRPaymentData build() {
                QRPaymentData data = new QRPaymentData();
                data.qrToken = this.qrToken;
                data.qrUrl = this.qrUrl;
                data.transaccionId = this.transaccionId;
                data.monto = this.monto;
                data.descripcion = this.descripcion;
                return data;
            }
        }
    }
}