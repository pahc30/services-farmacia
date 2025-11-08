package pe.com.farmaciadey.metodopago.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.farmaciadey.metodopago.services.QRScanDetectionService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para manejar la generación y detección de QR codes de pago
 * Permite detectar cuando un usuario escanea un QR con su celular
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/qr")
@CrossOrigin(origins = "*")
public class QRScanController {

    @Autowired
    private QRScanDetectionService qrScanService;

    /**
     * Generar QR de pago para una transacción
     * Se llama después de crear el PaymentIntent
     */
    @PostMapping("/generar/{transaccionId}")
    public ResponseEntity<?> generarQRPago(@PathVariable("transaccionId") Long transaccionId) {
        try {
            log.info("🔲 Generando QR de pago para transacción: {}", transaccionId);

            QRScanDetectionService.QRPaymentData qrData = qrScanService.generarQRPago(transaccionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrToken", qrData.getQrToken());
            response.put("qrUrl", qrData.getQrUrl());
            response.put("transaccionId", qrData.getTransaccionId());
            response.put("monto", qrData.getMonto());
            response.put("descripcion", qrData.getDescripcion());
            response.put("message", "QR generado exitosamente");

            log.info("✅ QR generado exitosamente: {}", qrData.getQrToken());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error generando QR: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error generando QR de pago");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint que se llama cuando alguien escanea el QR
     * Esta URL estará dentro del QR code
     */
    @GetMapping("/scan/{qrToken}")
    public ResponseEntity<?> escanearQR(@PathVariable("qrToken") String qrToken,
                                       @RequestParam(value = "t", required = false) String timestamp,
                                       @RequestParam(value = "amt", required = false) String amount) {
        try {
            log.info("📱 QR escaneado detectado: {} (timestamp: {}, amount: {})", 
                    qrToken, timestamp, amount);

            boolean procesado = qrScanService.procesarEscaneoQR(qrToken);

            if (procesado) {
                // Página de confirmación que se muestra en el celular
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
    @GetMapping("/estado/{qrToken}")
    public ResponseEntity<?> verificarEstadoQR(@PathVariable("qrToken") String qrToken) {
        try {
            Map<String, Object> estado = qrScanService.obtenerEstadoQR(qrToken);
            return ResponseEntity.ok(estado);

        } catch (Exception e) {
            log.error("❌ Error verificando estado QR: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valido", false);
            errorResponse.put("mensaje", "Error interno del servidor");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint API para confirmar escaneo (alternativo al GET)
     */
    @PostMapping("/confirmar-escaneo/{qrToken}")
    public ResponseEntity<?> confirmarEscaneo(@PathVariable("qrToken") String qrToken) {
        try {
            log.info("📱 Confirmando escaneo manual para QR: {}", qrToken);

            boolean procesado = qrScanService.procesarEscaneoQR(qrToken);

            Map<String, Object> response = new HashMap<>();
            response.put("success", procesado);
            response.put("qrToken", qrToken);
            
            if (procesado) {
                response.put("message", "Escaneo confirmado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "QR no válido o ya procesado");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("❌ Error confirmando escaneo: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor");
            
            return ResponseEntity.status(500).body(errorResponse);
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
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"error-icon\">❌</div>");
        html.append("<h1>Error</h1>");
        html.append("<p>").append(mensajeError).append("</p>");
        html.append("<p>Por favor, intenta nuevamente o contacta al soporte.</p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}