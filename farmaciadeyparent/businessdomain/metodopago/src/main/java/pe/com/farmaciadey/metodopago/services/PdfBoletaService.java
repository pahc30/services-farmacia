package pe.com.farmaciadey.metodopago.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.farmaciadey.metodopago.models.TransaccionPago;
import pe.com.farmaciadey.metodopago.repository.TransaccionPagoRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Servicio para generar boletas de venta en PDF
 */
@Slf4j
@Service
public class PdfBoletaService {

    @Autowired
    private TransaccionPagoRepository transaccionRepository;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera una boleta PDF para una transacción específica
     */
    public byte[] generarBoletaPdf(Long transaccionId) throws Exception {
        Optional<TransaccionPago> transaccionOpt = transaccionRepository.findById(transaccionId);
        
        if (transaccionOpt.isEmpty()) {
            throw new RuntimeException("Transacción no encontrada: " + transaccionId);
        }

        TransaccionPago transaccion = transaccionOpt.get();
        
        if (!TransaccionPago.EstadoTransaccion.COMPLETADA.equals(transaccion.getEstado())) {
            throw new RuntimeException("Solo se pueden generar boletas para transacciones completadas");
        }

        return generarPdfInterno(transaccion);
    }

    /**
     * Genera una boleta PDF por ID de compra
     */
    public byte[] generarBoletaPorCompra(Long compraId) throws Exception {
        Optional<TransaccionPago> transaccionOpt = transaccionRepository
                .findByCompraIdAndEstado(compraId, TransaccionPago.EstadoTransaccion.COMPLETADA);
        
        if (transaccionOpt.isEmpty()) {
            throw new RuntimeException("No se encontró transacción completada para la compra: " + compraId);
        }

        return generarPdfInterno(transaccionOpt.get());
    }

    private byte[] generarPdfInterno(TransaccionPago transaccion) throws Exception {
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
        agregarInformacionCliente(document, transaccion, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Tabla de productos/servicios
        agregarTablaProductos(document, transaccion, headerFont, normalFont);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Totales
        agregarTotales(document, transaccion, headerFont, normalFont);
        
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

    private void agregarInformacionCliente(Document document, TransaccionPago transaccion, Font normalFont) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 2, 1, 2});
        
        infoTable.addCell(new PdfPCell(new Phrase("Fecha:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(transaccion.getFechaCreacion().format(DATE_FORMATTER), normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Compra ID:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(transaccion.getCompraId().toString(), normalFont)));
        
        infoTable.addCell(new PdfPCell(new Phrase("Cliente:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Cliente General", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Método Pago:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Pago Simulado", normalFont)));
        
        infoTable.addCell(new PdfPCell(new Phrase("Transacción:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(transaccion.getReferenciaExterna() != null ? transaccion.getReferenciaExterna() : transaccion.getId().toString(), normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase("Estado:", normalFont)));
        infoTable.addCell(new PdfPCell(new Phrase(transaccion.getEstado().toString(), normalFont)));
        
        document.add(infoTable);
    }

    private void agregarTablaProductos(Document document, TransaccionPago transaccion, Font headerFont, Font normalFont) throws DocumentException {
        PdfPTable productTable = new PdfPTable(5);
        productTable.setWidthPercentage(100);
        productTable.setWidths(new float[]{3, 1, 2, 2, 2});
        
        // Headers
        productTable.addCell(new PdfPCell(new Phrase("Descripción", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Cant.", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("P. Unit.", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Descuento", headerFont)));
        productTable.addCell(new PdfPCell(new Phrase("Importe", headerFont)));
        
        // Datos del producto (simulado - en una implementación real vendría de la compra)
        productTable.addCell(new PdfPCell(new Phrase(transaccion.getDescripcion() != null ? transaccion.getDescripcion() : "Compra en Farmacia DeY", normalFont)));
        productTable.addCell(new PdfPCell(new Phrase("1", normalFont)));
        productTable.addCell(new PdfPCell(new Phrase(DECIMAL_FORMAT.format(transaccion.getMonto()), normalFont)));
        productTable.addCell(new PdfPCell(new Phrase("0.00", normalFont)));
        productTable.addCell(new PdfPCell(new Phrase(DECIMAL_FORMAT.format(transaccion.getMonto()), normalFont)));
        
        document.add(productTable);
    }

    private void agregarTotales(Document document, TransaccionPago transaccion, Font headerFont, Font normalFont) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(60);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        BigDecimal montoTransaccion = transaccion.getMonto();
        BigDecimal subtotal = montoTransaccion.divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP); // Asumiendo IGV 18%
        BigDecimal igv = montoTransaccion.subtract(subtotal);
        
        totalsTable.addCell(new PdfPCell(new Phrase("Sub Total:", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(subtotal), normalFont)));
        
        totalsTable.addCell(new PdfPCell(new Phrase("IGV (18%):", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(igv), normalFont)));
        
        totalsTable.addCell(new PdfPCell(new Phrase("Descuentos:", normalFont)));
        totalsTable.addCell(new PdfPCell(new Phrase("S/ 0.00", normalFont)));
        
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL:", headerFont));
        totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalsTable.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(montoTransaccion), headerFont));
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