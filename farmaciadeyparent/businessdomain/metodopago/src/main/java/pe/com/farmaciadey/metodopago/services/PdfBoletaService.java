package pe.com.farmaciadey.metodopago.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.com.farmaciadey.metodopago.models.TransaccionPago;
import pe.com.farmaciadey.metodopago.models.Metodopago;
import pe.com.farmaciadey.metodopago.repository.TransaccionPagoRepository;
import pe.com.farmaciadey.metodopago.repository.MetodopagoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para generar boletas de venta en PDF
 */
@Slf4j
@Service
public class PdfBoletaService {

    @Autowired
    private TransaccionPagoRepository transaccionRepository;
    
    @Autowired
    private MetodopagoRepository metodopagoRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PdfBoletaService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final ZoneId PERU_TIMEZONE = ZoneId.of("America/Lima"); // UTC-5

    /**
     * Genera una boleta PDF para una transacción específica
     */
    public byte[] generarBoletaPorTransaccion(Long transaccionId) throws Exception {
        log.info("🔍 Generando boleta para transacción: {}", transaccionId);
        
        Optional<TransaccionPago> transaccionOpt = transaccionRepository.findById(transaccionId);
        
        if (transaccionOpt.isEmpty()) {
            throw new RuntimeException("Transacción no encontrada: " + transaccionId);
        }

        TransaccionPago transaccion = transaccionOpt.get();
        
        if (!TransaccionPago.EstadoTransaccion.COMPLETADA.equals(transaccion.getEstado())) {
            throw new RuntimeException("Solo se pueden generar boletas para transacciones completadas");
        }

        // Obtener información de la compra relacionada
        JsonNode compraInfo = obtenerInformacionCompra(transaccion.getCompraId().intValue());
        
        return generarPdfInterno(transaccion, compraInfo);
    }

    /**
     * Genera una boleta PDF por ID de compra
     */
    public byte[] generarBoletaPorCompra(Long compraId) throws DocumentException, Exception {
        // Buscar la transacción asociada a esta compra
        // En un caso real, podrías tener múltiples transacciones por compra, pero aquí asumimos una
        JsonNode compraInfo = obtenerInformacionCompra(compraId.intValue());
        
        // Primero intentar encontrar una transacción completada
        Optional<TransaccionPago> transaccionOpt = transaccionRepository
                .findByCompraIdAndEstado(compraId, TransaccionPago.EstadoTransaccion.COMPLETADA);
        
        // Si no hay transacción completada, buscar cualquier transacción de la compra
        if (transaccionOpt.isEmpty()) {
            List<TransaccionPago> transacciones = transaccionRepository.findByCompraId(compraId);
            if (transacciones.isEmpty()) {
                // Si no hay transacciones, crear una transacción simulada para la boleta
                TransaccionPago transaccionSimulada = createSimulatedTransaction(compraId, compraInfo);
                return generarPdfInterno(transaccionSimulada, compraInfo);
            }
            // Tomar la primera transacción disponible
            transaccionOpt = Optional.of(transacciones.get(0));
        }

        return generarPdfInterno(transaccionOpt.get(), compraInfo);
    }

    /**
     * Obtiene información detallada de la compra desde el microservicio de compras
     */
    private JsonNode obtenerInformacionCompra(Integer compraId) {
        try {
            log.info("🔄 Obteniendo información de compra: {}", compraId);
            
            String response = webClient.get()
                .uri("http://farmacia-compra:7015/compra/api/compra/find/" + compraId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            JsonNode responseNode = objectMapper.readTree(response);
            if (responseNode.has("dato") && !responseNode.get("dato").isNull()) {
                JsonNode compraData = responseNode.get("dato");
                log.info("✅ Información de compra obtenida: ID {}", compraId);
                return compraData;
            }
            
            log.warn("⚠️ No se encontró información de compra para ID: {}", compraId);
            return objectMapper.createObjectNode();
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo información de compra {}: {}", compraId, e.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    /**
     * Obtiene información del usuario desde el microservicio de usuarios
     */
    private JsonNode obtenerInformacionUsuario(Long usuarioId) {
        try {
            log.info("🔄 Obteniendo información de usuario: {}", usuarioId);
            
            String response = webClient.post()
                .uri("http://farmacia-usuario:7012/usuario/api/usuarios/find/" + usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}") 
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            JsonNode responseNode = objectMapper.readTree(response);
            if (responseNode.has("dato") && !responseNode.get("dato").isNull()) {
                JsonNode userData = responseNode.get("dato");
                log.info("✅ Información de usuario obtenida: ID {}", usuarioId);
                return userData;
            }
            
            log.warn("⚠️ No se encontró información de usuario para ID: {}", usuarioId);
            return objectMapper.createObjectNode();
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo información de usuario {}: {}", usuarioId, e.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    /**
     * Crea una transacción simulada para generar boletas de compras sin proceso de pago
     */
    private TransaccionPago createSimulatedTransaction(Long compraId, JsonNode compraInfo) {
        log.info("🔄 Creando transacción simulada para compra: {}", compraId);
        
        TransaccionPago transaccionSimulada = new TransaccionPago();
        transaccionSimulada.setId(999999L + compraId); // ID simulado único
        transaccionSimulada.setCompraId(compraId);
        // Obtener el método de pago real desde la compra
        Long metodoPagoId = 1L; // Por defecto Yape/Plin
        if (compraInfo.has("metodoPagoId") && !compraInfo.get("metodoPagoId").isNull()) {
            metodoPagoId = compraInfo.get("metodoPagoId").asLong();
        }
        transaccionSimulada.setMetodoPagoId(metodoPagoId); // Método de pago real
        
        // Obtener el total real de la compra si está disponible
        BigDecimal montoReal = BigDecimal.valueOf(100.00); // Valor por defecto
        if (compraInfo.has("total") && !compraInfo.get("total").isNull()) {
            montoReal = new BigDecimal(compraInfo.get("total").asText());
        }
        
        transaccionSimulada.setMonto(montoReal);
        transaccionSimulada.setMoneda("PEN");
        transaccionSimulada.setEstado(TransaccionPago.EstadoTransaccion.COMPLETADA);
        transaccionSimulada.setDescripcion("Compra en Farmacia DeY - Boleta generada sin transacción de pago");
        transaccionSimulada.setReferenciaExterna("SIM-" + compraId);
        transaccionSimulada.setFechaCreacion(java.time.LocalDateTime.now());
        transaccionSimulada.setFechaPago(java.time.LocalDateTime.now());
        
        return transaccionSimulada;
    }

    private byte[] generarPdfInterno(TransaccionPago transaccion, JsonNode compraInfo) throws Exception {
        log.info("📄 Generando boleta PDF para transacción: {}", transaccion.getId());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Fuentes
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
        
        // Header - Logo y datos empresa
        agregarHeaderEmpresa(document, titleFont, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Título de la boleta
        Paragraph titulo = new Paragraph("BOLETA DE VENTA ELECTRÓNICA", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        
        // Número de boleta
        Paragraph numeroBoleta = new Paragraph("B001-" + String.format("%08d", transaccion.getId()), headerFont);
        numeroBoleta.setAlignment(Element.ALIGN_CENTER);
        document.add(numeroBoleta);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Información del cliente y transacción
        agregarInformacionCliente(document, transaccion, compraInfo, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Tabla de productos/servicios
        agregarTablaProductos(document, transaccion, compraInfo, headerFont, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Totales
        agregarTotales(document, transaccion, compraInfo, headerFont, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Footer con información adicional
        agregarFooter(document, transaccion, smallFont);
        
        document.close();
        
        log.info("✅ Boleta PDF generada exitosamente para transacción: {}", transaccion.getId());
        return baos.toByteArray();
    }

    private void agregarHeaderEmpresa(Document document, Font titleFont, Font normalFont) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2, 1});
        
        // Logo/Nombre empresa
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.addElement(new Paragraph("FARMACIA DeY", titleFont));
        logoCell.addElement(new Paragraph("Av. Principal 123, Lima, Perú", normalFont));
        logoCell.addElement(new Paragraph("RUC: 20123456789", normalFont));
        logoCell.addElement(new Paragraph("Teléfono: (01) 234-5678", normalFont));
        headerTable.addCell(logoCell);
        
        // Datos fiscales
        PdfPCell fiscalCell = new PdfPCell();
        fiscalCell.setBorder(Rectangle.BOX);
        fiscalCell.addElement(new Paragraph("R.U.C: 20123456789", normalFont));
        fiscalCell.addElement(new Paragraph("", normalFont));
        fiscalCell.addElement(new Paragraph("BOLETA DE VENTA", normalFont));
        fiscalCell.addElement(new Paragraph("ELECTRÓNICA", normalFont));
        headerTable.addCell(fiscalCell);
        
        document.add(headerTable);
    }

    private void agregarInformacionCliente(Document document, TransaccionPago transaccion, JsonNode compraInfo, Font normalFont) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 2, 1, 2});
        
        // Obtener información del usuario desde la compra
        String nombreCliente = "Cliente General";
        String metodoPago = "Efectivo";
        
        log.info("🔍 Información de compra recibida: {}", compraInfo != null ? compraInfo.toString() : "null");
        
        if (compraInfo != null && compraInfo.has("usuarioId")) {
            try {
                Long usuarioId = compraInfo.get("usuarioId").asLong();
                log.info("🔍 Obteniendo información del usuario ID: {}", usuarioId);
                JsonNode usuarioInfo = obtenerInformacionUsuario(usuarioId);
                
                log.info("🔍 Información de usuario recibida: {}", usuarioInfo != null ? usuarioInfo.toString() : "null");
                
                if (usuarioInfo != null) {
                    String nombres = usuarioInfo.has("nombres") ? usuarioInfo.get("nombres").asText() : "";
                    String apellidos = usuarioInfo.has("apellidos") ? usuarioInfo.get("apellidos").asText() : "";
                    
                    log.info("🔍 Datos extraídos - Nombres: '{}', Apellidos: '{}'", nombres, apellidos);
                    
                    if (!nombres.isEmpty() && !apellidos.isEmpty()) {
                        nombreCliente = nombres + " " + apellidos;
                    } else if (!nombres.isEmpty()) {
                        nombreCliente = nombres;
                    } else if (!apellidos.isEmpty()) {
                        nombreCliente = apellidos;
                    }
                    
                    log.info("✅ Cliente final: '{}'", nombreCliente);
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener información del usuario: {}", e.getMessage());
            }
        }
        
        // Obtener método de pago real
        try {
            if (transaccion.getMetodoPagoId() != null) {
                Optional<Metodopago> metodoOpt = metodopagoRepository.findById(transaccion.getMetodoPagoId().intValue());
                if (metodoOpt.isPresent()) {
                    metodoPago = metodoOpt.get().getTipo();
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener información del método de pago: {}", e.getMessage());
        }
        
        infoTable.addCell(new PdfPCell(new Phrase("Fecha:", normalFont)));
        // Convertir fecha a zona horaria de Perú (UTC-5)
        LocalDateTime fechaPeru = transaccion.getFechaCreacion().atZone(ZoneId.systemDefault()).withZoneSameInstant(PERU_TIMEZONE).toLocalDateTime();
        infoTable.addCell(new PdfPCell(new Phrase(fechaPeru.format(DATE_FORMATTER), normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Cliente:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(nombreCliente, normalFont)));
        
        infoTable.addCell(new PdfPCell(new Phrase("Método Pago:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(metodoPago, normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Transacción:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(transaccion.getReferenciaExterna() != null ? transaccion.getReferenciaExterna() : transaccion.getId().toString(), normalFont)));
        
        document.add(infoTable);
    }

    private void agregarTablaProductos(Document document, TransaccionPago transaccion, JsonNode compraInfo, Font headerFont, Font normalFont) throws DocumentException {
        log.info("🔍 Iniciando agregarTablaProductos");
        log.info("🔍 compraInfo disponible: {}", compraInfo != null ? "Sí" : "No");
        
        if (compraInfo != null) {
            log.info("🔍 Estructura completa de compraInfo: {}", compraInfo.toString());
        }
        
        PdfPTable productTable = new PdfPTable(5);
        productTable.setWidthPercentage(100);
        productTable.setWidths(new float[]{3, 1, 2, 2, 2});
        
        // Headers
        productTable.addCell(new PdfPCell(new Phrase("Descripción", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Cant.", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("P. Unit.", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Descuento", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Importe", headerFont)));
        
        // Obtener productos reales de la compra
        if (compraInfo != null && compraInfo.has("detalleCompra") && compraInfo.get("detalleCompra").isArray()) {
            for (JsonNode detalle : compraInfo.get("detalleCompra")) {
                String descripcion = "Producto";
                String cantidad = "1";
                String precioUnitario = "0.00";
                String descuento = "0.00";
                String importe = "0.00";
                
                // Obtener nombre del producto
                if (detalle.has("producto") && detalle.get("producto").has("nombre")) {
                    descripcion = detalle.get("producto").get("nombre").asText();
                }
                
                // Obtener cantidad
                if (detalle.has("cantidad")) {
                    cantidad = String.valueOf(detalle.get("cantidad").asInt());
                }
                
                // Obtener precio unitario real del producto
                if (detalle.has("producto") && detalle.get("producto").has("precio")) {
                    precioUnitario = DECIMAL_FORMAT.format(detalle.get("producto").get("precio").asDouble());
                } else if (detalle.has("precioUnitario")) {
                    precioUnitario = DECIMAL_FORMAT.format(detalle.get("precioUnitario").asDouble());
                }
                
                // Calcular importe (cantidad * precio unitario)
                if (detalle.has("subtotal")) {
                    importe = DECIMAL_FORMAT.format(detalle.get("subtotal").asDouble());
                } else {
                    // Calcular si no está disponible el subtotal
                    int cant = detalle.has("cantidad") ? detalle.get("cantidad").asInt() : 1;
                    double precio = 0.0;
                    if (detalle.has("producto") && detalle.get("producto").has("precio")) {
                        precio = detalle.get("producto").get("precio").asDouble();
                    } else if (detalle.has("precioUnitario")) {
                        precio = detalle.get("precioUnitario").asDouble();
                    }
                    importe = DECIMAL_FORMAT.format(cant * precio);
                }
                
                productTable.addCell(new PdfPCell(new Phrase(descripcion, normalFont)));
                productTable.addCell(new PdfPCell(new Phrase(cantidad, normalFont)));
                productTable.addCell(new PdfPCell(new Phrase(precioUnitario, normalFont)));
                productTable.addCell(new PdfPCell(new Phrase(descuento, normalFont)));
                productTable.addCell(new PdfPCell(new Phrase(importe, normalFont)));
            }
        } else {
            // Fallback si no hay información de productos - usar descripción más específica
            String descripcionCompra = transaccion.getDescripcion() != null && !transaccion.getDescripcion().isEmpty() 
                ? transaccion.getDescripcion() 
                : "Compra en Farmacia DeY - Boleta generada sin transacción de pago";
            productTable.addCell(new PdfPCell(new Phrase(descripcionCompra, normalFont)));
            productTable.addCell(new PdfPCell(new Phrase("1", normalFont)));
            productTable.addCell(new PdfPCell(new Phrase(DECIMAL_FORMAT.format(transaccion.getMonto()), normalFont)));
            productTable.addCell(new PdfPCell(new Phrase("0.00", normalFont)));
            productTable.addCell(new PdfPCell(new Phrase(DECIMAL_FORMAT.format(transaccion.getMonto()), normalFont)));
        }
        
        document.add(productTable);
    }

    private void agregarTotales(Document document, TransaccionPago transaccion, JsonNode compraInfo, Font headerFont, Font normalFont) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(60);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        // Los precios ya incluyen IGV, usar el subtotal de la compra como monto real
        BigDecimal montoRealConIgv = transaccion.getMonto();
        
        // Si hay información de compra, usar el subtotal (que es el precio real con IGV)
        if (compraInfo != null && compraInfo.has("subtotal") && !compraInfo.get("subtotal").isNull()) {
            montoRealConIgv = new BigDecimal(compraInfo.get("subtotal").asText());
        }
        
        // Calcular desglose: subtotal sin IGV e IGV por separado
        BigDecimal subtotal = montoRealConIgv.divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP); // Precio base sin IGV
        BigDecimal igv = montoRealConIgv.subtract(subtotal); // Monto del IGV
        
        totalsTable.addCell(new PdfPCell(new Phrase("Sub Total:", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(subtotal), normalFont)));
        
        totalsTable.addCell(new PdfPCell(new Phrase("IGV (18%):", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(igv), normalFont)));
        
        totalsTable.addCell(new PdfPCell(new Phrase("Descuentos:", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ 0.00", normalFont)));
        
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL:", headerFont));
        totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalsTable.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(montoRealConIgv), headerFont));
        totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalsTable.addCell(totalValueCell);
        
        document.add(totalsTable);
    }

    private void agregarFooter(Document document, TransaccionPago transaccion, Font smallFont) throws DocumentException {
        document.add(new Paragraph(" ")); // Espacio
        
        Paragraph agradecimiento = new Paragraph("¡Gracias por su compra!", smallFont);
        agradecimiento.setAlignment(Element.ALIGN_CENTER);
        document.add(agradecimiento);
        
        document.add(new Paragraph(" ")); // Espacio
        
        Paragraph condiciones = new Paragraph(
            "• Esta boleta ha sido generada electrónicamente\n" +
            "• Conserve este documento para futuras referencias\n" +
            "• Para consultas: info@farmaciadey.com\n" +
            "• Pago procesado con sistema simulado para pruebas", 
            smallFont
        );
        condiciones.setAlignment(Element.ALIGN_LEFT);
        document.add(condiciones);
        
        document.add(new Paragraph(" ")); // Espacio
        
        Paragraph hash = new Paragraph("ID Transacción: " + (transaccion.getReferenciaExterna() != null ? transaccion.getReferenciaExterna() : transaccion.getId().toString()), smallFont);
        hash.setAlignment(Element.ALIGN_CENTER);
        document.add(hash);
    }
}