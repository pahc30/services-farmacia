package pe.com.farmaciadey.metodopago.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.farmaciadey.metodopago.models.requests.PaymentIntentRequest;
import pe.com.farmaciadey.metodopago.models.responses.PaymentIntentResponse;
import pe.com.farmaciadey.metodopago.services.SimulatedPaymentService;
import pe.com.farmaciadey.metodopago.services.PdfBoletaService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para manejar pagos con sistema simulado y boletas PDF
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private SimulatedPaymentService paymentService;

    @Autowired
    private PdfBoletaService pdfBoletaService;

    /**
     * Crear un nuevo PaymentIntent usando el sistema simulado
     */
    @PostMapping("/crear-intent")
    public ResponseEntity<?> crearPaymentIntent(@Valid @RequestBody PaymentIntentRequest request) {
        try {
            log.info("💳 Creando PaymentIntent simulado para compra: {} por monto: {}", 
                    request.getCompraId(), request.getMonto());

            PaymentIntentResponse response = paymentService.createPaymentIntent(request);

            if (response.isSuccess()) {
                log.info("✅ PaymentIntent simulado creado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                log.error("❌ Error creando PaymentIntent simulado: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Error inesperado al crear PaymentIntent: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Confirmar un pago simulado
     */
    @PostMapping("/confirmar/{transaccionId}")
    public ResponseEntity<?> confirmarPago(@PathVariable Long transaccionId) {
        try {
            log.info("✅ Confirmando pago simulado para transacción: {}", transaccionId);

            boolean confirmado = paymentService.confirmarPago(transaccionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", confirmado);
            response.put("transaccionId", transaccionId);
            
            if (confirmado) {
                response.put("message", "Pago confirmado exitosamente");
                log.info("✅ Pago confirmado para transacción: {}", transaccionId);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No se pudo confirmar el pago");
                log.error("❌ Error confirmando pago para transacción: {}", transaccionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Error confirmando pago: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener estado de una transacción
     */
    @GetMapping("/transaccion/{transaccionId}")
    public ResponseEntity<?> obtenerEstadoTransaccion(@PathVariable Long transaccionId) {
        try {
            log.info("🔍 Consultando estado de transacción: {}", transaccionId);

            return ResponseEntity.ok(paymentService.obtenerEstadoPago(transaccionId));

        } catch (Exception e) {
            log.error("❌ Error obteniendo estado de transacción: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todas las transacciones de una compra
     */
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<?> obtenerTransaccionesPorCompra(@PathVariable Long compraId) {
        try {
            log.info("🔍 Consultando transacciones para compra: {}", compraId);

            return ResponseEntity.ok(paymentService.obtenerTransaccionesPorCompra(compraId));

        } catch (Exception e) {
            log.error("❌ Error obteniendo transacciones por compra: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Descargar boleta de venta en PDF por ID de transacción
     */
    @GetMapping("/boleta/transaccion/{transaccionId}")
    public ResponseEntity<?> descargarBoletaPorTransaccion(@PathVariable Long transaccionId) {
        try {
            log.info("📄 Generando boleta PDF para transacción: {}", transaccionId);

            byte[] pdfBytes = pdfBoletaService.generarBoletaPdf(transaccionId);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boleta_" + transaccionId + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            log.info("✅ Boleta PDF generada exitosamente para transacción: {}", transaccionId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            log.error("❌ Error generando boleta PDF: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error generando boleta PDF");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Descargar boleta de venta en PDF por ID de compra
     */
    @GetMapping("/boleta/compra/{compraId}")
    public ResponseEntity<?> descargarBoletaPorCompra(@PathVariable Long compraId) {
        try {
            log.info("📄 Generando boleta PDF para compra: {}", compraId);

            byte[] pdfBytes = pdfBoletaService.generarBoletaPorCompra(compraId);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boleta_compra_" + compraId + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            log.info("✅ Boleta PDF generada exitosamente para compra: {}", compraId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            log.error("❌ Error generando boleta PDF: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error generando boleta PDF");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check del sistema de pagos
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("service", "Método de Pago Simulado");
            response.put("provider", "Sistema Simulado");
            response.put("provider_available", paymentService.isAvailable());
            response.put("pdf_service", "Disponible");
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("💚 Health check - Sistema funcionando correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error en health check: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Endpoint para testing - simular diferentes tipos de errores
     */
    @PostMapping("/test/simular-error/{tipoError}")
    public ResponseEntity<?> simularError(@PathVariable String tipoError) {
        try {
            log.info("🧪 Simulando error tipo: {}", tipoError);
            
            return ResponseEntity.ok(paymentService.simularError(tipoError));
            
        } catch (Exception e) {
            log.error("❌ Error en simulación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener información del sistema
     */
    @GetMapping("/info")
    public ResponseEntity<?> obtenerInformacion() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("sistema", "Farmacia DeY - Método de Pago");
            response.put("version", "1.0.0");
            response.put("provider", "Sistema Simulado (GRATUITO)");
            response.put("features", new String[]{
                "Pagos simulados",
                "Generación de boletas PDF",
                "Estados de transacción",
                "Testing de errores",
                "Sin costo de transacción"
            });
            response.put("endpoints", new String[]{
                "POST /crear-intent - Crear pago simulado",
                "POST /confirmar/{id} - Confirmar pago",
                "GET /transaccion/{id} - Estado de transacción",
                "GET /compra/{id} - Transacciones por compra",
                "GET /boleta/transaccion/{id} - Descargar boleta por transacción",
                "GET /boleta/compra/{id} - Descargar boleta por compra",
                "GET /health - Health check",
                "GET /info - Esta información"
            });
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo información: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}