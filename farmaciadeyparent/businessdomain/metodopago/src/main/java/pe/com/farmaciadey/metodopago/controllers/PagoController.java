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
import pe.com.farmaciadey.metodopago.services.QRScanDetectionService;

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

    @Autowired
    private QRScanDetectionService qrScanService;

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
    public ResponseEntity<?> confirmarPago(@PathVariable("transaccionId") Long transaccionId) {
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
    public ResponseEntity<?> obtenerEstadoTransaccion(@PathVariable("transaccionId") Long transaccionId) {
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
    public ResponseEntity<?> obtenerTransaccionesPorCompra(@PathVariable("compraId") Long compraId) {
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
    public ResponseEntity<?> descargarBoletaPorTransaccion(@PathVariable("transaccionId") Long transaccionId) {
        try {
            log.info("📄 Generando boleta PDF para transacción: {}", transaccionId);

            byte[] pdfBytes = pdfBoletaService.generarBoletaPorTransaccion(transaccionId);

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
    public ResponseEntity<?> descargarBoletaPorCompra(@PathVariable("compraId") Long compraId) {
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
    public ResponseEntity<?> simularError(@PathVariable("tipoError") String tipoError) {
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
                "GET /info - Esta información",
                "",
                "=== QR SCAN DETECTION ===",
                "POST /api/v1/qr/generar/{transaccionId} - Generar QR de pago",
                "GET /api/v1/qr/scan/{qrToken} - Escanear QR (URL del QR)",
                "GET /api/v1/qr/estado/{qrToken} - Verificar estado del QR",
                "POST /api/v1/qr/confirmar-escaneo/{qrToken} - Confirmar escaneo manual"
            });
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo información: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ENDPOINTS QR ==========

    /**
     * Generar QR de pago para una transacción
     */
    @PostMapping("/qr/generar/{transaccionId}")
    public ResponseEntity<?> generarQRPago(@PathVariable("transaccionId") Long transaccionId) {
        try {
            log.info("🔲 Generando QR para transacción: {}", transaccionId);
            
            var qrData = qrScanService.generarQRPago(transaccionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrToken", qrData.getQrToken());
            response.put("qrUrl", qrData.getQrUrl());
            response.put("transaccionId", qrData.getTransaccionId());
            response.put("monto", qrData.getMonto());
            response.put("descripcion", qrData.getDescripcion());
            
            log.info("✅ QR generado exitosamente: {}", qrData.getQrToken());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error generando QR: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint que se llama cuando se escanea el QR (URL dentro del QR)
     */
    @GetMapping("/qr/scan/{qrToken}")
    public ResponseEntity<String> escanearQR(
            @PathVariable("qrToken") String qrToken,
            @RequestParam(value = "t", required = false) String timestamp,
            @RequestParam(value = "amt", required = false) String amount) {
        
        try {
            log.info("📱 QR escaneado: Token={}, Timestamp={}, Amount={}", 
                    qrToken, timestamp, amount);

            boolean procesado = qrScanService.procesarEscaneoQR(qrToken);

            if (procesado) {
                String html = generarPaginaConfirmacion(qrToken, amount);
                return ResponseEntity.ok()
                        .header("Content-Type", "text/html; charset=UTF-8")
                        .body(html);
            } else {
                String htmlError = generarPaginaError("QR no válido o ya procesado");
                return ResponseEntity.badRequest()
                        .header("Content-Type", "text/html; charset=UTF-8")
                        .body(htmlError);
            }

        } catch (Exception e) {
            log.error("❌ Error procesando escaneo QR: {}", e.getMessage(), e);
            String htmlError = generarPaginaError("Error interno del servidor");
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(htmlError);
        }
    }

    /**
     * Verificar si un QR ha sido escaneado (para polling desde frontend)
     */
    @GetMapping("/qr/estado/{qrToken}")
    public ResponseEntity<?> verificarEstadoQR(@PathVariable("qrToken") String qrToken) {
        try {
            log.info("🔍 Verificando estado QR: {}", qrToken);
            
            // Consultar el estado real del QR desde el servicio
            boolean escaneado = qrScanService.verificarEscaneoQR(qrToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrToken", qrToken);
            response.put("escaneado", escaneado);
            response.put("fechaEscaneo", escaneado ? System.currentTimeMillis() : null);
            response.put("transaccionId", 1L);
            
            log.info("📊 Estado QR {}: escaneado={}", qrToken, escaneado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error verificando estado QR: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Simular escaneo de QR para testing
     */
    @PostMapping("/qr/confirmar-escaneo/{qrToken}")
    public ResponseEntity<?> confirmarEscaneoQR(@PathVariable("qrToken") String qrToken) {
        try {
            log.info("🧪 Simulando escaneo QR: {}", qrToken);
            
            boolean procesado = qrScanService.procesarEscaneoQR(qrToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", procesado);
            response.put("message", procesado ? "QR escaneado exitosamente" : "QR no válido o ya procesado");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error confirmando escaneo QR: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private String generarPaginaConfirmacion(String qrToken, String amount) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"es\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>QR Escaneado - Farmacia Dey</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; text-align: center; padding: 20px; ");
        html.append("background: linear-gradient(135deg, #4CAF50, #45a049); color: white; margin: 0; }");
        html.append(".container { max-width: 400px; margin: 50px auto; background: white; color: #333; ");
        html.append("padding: 30px; border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }");
        html.append(".success-icon { font-size: 60px; color: #4CAF50; margin-bottom: 20px; }");
        html.append(".amount { font-size: 24px; font-weight: bold; color: #2196F3; margin: 15px 0; }");
        html.append(".message { font-size: 16px; line-height: 1.5; margin: 20px 0; }");
        html.append(".qr-token { background: #f5f5f5; padding: 10px; border-radius: 5px; ");
        html.append("font-family: monospace; font-size: 12px; margin: 10px 0; word-break: break-all; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"success-icon\">✅</div>");
        html.append("<h1>¡QR Escaneado!</h1>");
        html.append("<div class=\"message\">Tu pago ha sido detectado exitosamente</div>");
        if (amount != null) {
            html.append("<div class=\"amount\">Monto: S/ ").append(amount).append("</div>");
        }
        html.append("<div class=\"message\">Puedes cerrar esta ventana.<br>La compra se completará automáticamente.</div>");
        html.append("<div class=\"qr-token\">Token: ").append(qrToken).append("</div>");
        html.append("</div>");
        html.append("<script>");
        html.append("setTimeout(() => {");
        html.append("if (window.close) { window.close(); }");
        html.append("else { document.body.innerHTML = '<div style=\"text-align:center;padding:50px;\"><h2>✅ Listo! Puedes cerrar esta ventana</h2></div>'; }");
        html.append("}, 3000);");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    private String generarPaginaError(String mensajeError) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"es\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>Error QR - Farmacia Dey</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; text-align: center; padding: 20px; ");
        html.append("background: linear-gradient(135deg, #f44336, #d32f2f); color: white; margin: 0; }");
        html.append(".container { max-width: 400px; margin: 50px auto; background: white; color: #333; ");
        html.append("padding: 30px; border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }");
        html.append(".error-icon { font-size: 60px; color: #f44336; margin-bottom: 20px; }");
        html.append(".message { font-size: 16px; line-height: 1.5; margin: 20px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"error-icon\">❌</div>");
        html.append("<h1>Error QR</h1>");
        html.append("<div class=\"message\">").append(mensajeError).append("</div>");
        html.append("<div class=\"message\">Por favor, intenta de nuevo o contacta con soporte.</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}