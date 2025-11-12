package pe.com.farmaciadey.compra.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.models.responses.DataResponse;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import pe.com.farmaciadey.compra.services.CompraService;
import pe.com.farmaciadey.compra.services.CompraIntegrationService;

@RestController
@RequestMapping("/api/compra")
public class CompraController {

    @Autowired
   private CompraService service;

   @Autowired
   private CarritoCompraService carritoCompraService;

    @Autowired
    private CompraIntegrationService integrationService;

    @PostMapping(value = "/save")
    public ResponseEntity<Object> save(@RequestBody Compra request) throws Exception {
        DataResponse response = new DataResponse();
        try {

            request.setCodigo(service.generarCodigoCompra());
            request.setSubtotal(0.0);
            request.getDetalleCompra().forEach(x -> {               
                
                ProductoResponse producto = getProducto(x.getProductoId());
                if (producto == null) {
                    throw new RuntimeException("El producto no existe");
                }
    
                if (producto.getStock() < x.getCantidad()) {
                    throw new RuntimeException("El producto no cuenta con stock suficiente");
                }

                x.setCompra(request);
                x.setPrecio(producto.getPrecio());
                x.setSubtotal(producto.getPrecio() * x.getCantidad());
                request.setSubtotal(request.getSubtotal() + x.getSubtotal());
            });

            response.setDato(service.save(request));

            request.getDetalleCompra().forEach(x -> { 
                updateStock(x.getProductoId(), x.getCantidad());
                // Solo eliminar del carrito si tiene carritoCompraId (cuando viene desde web)
                if (x.getCarritoCompraId() != null) {
                    carritoCompraService.delete(x.getCarritoCompraId());
                }
            });

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/find/{compraId}")
    public ResponseEntity<Object> find(@PathVariable("compraId") Integer compraId) throws Exception {
        DataResponse response = new DataResponse();
        try {
            Compra compra = service.findById(compraId);
            if (compra != null) {
                // Obtener información completa de productos y método de pago
                compra.setMetodoPago(getMetodoPago(compra.getMetodoPagoId()));
                
                compra.getDetalleCompra().forEach(d -> {
                    d.setProducto(getProducto(d.getProductoId()));
                });
                
                response.setDato(compra);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setException("Compra no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/list/{usuarioId}")
    public ResponseEntity<Object> list(@PathVariable("usuarioId") Integer usuarioId) throws Exception {
        DataResponse response = new DataResponse();
        try {
            List<Compra> compras = service.listByUsuario(usuarioId);
            compras.forEach(o -> {
                o.setMetodoPago(getMetodoPago(o.getMetodoPagoId()));

                o.getDetalleCompra().forEach(d -> {
                    d.setProducto(getProducto(d.getProductoId()));
                });
                
                // Corregir el cálculo del IGV para el frontend
                // Los precios de productos ya incluyen IGV
                // Para compras nuevas: total = subtotal (correcto)
                // Para compras viejas: puede tener total > subtotal (calculado mal antes)
                // Usamos el subtotal como base (suma de precios con IGV incluido)
                
                double totalConIgv = o.getSubtotal(); // Usar subtotal como precio real con IGV
                double subtotalSinIgv = totalConIgv / 1.18; // Precio base sin IGV  
                double montoIgv = totalConIgv - subtotalSinIgv; // Monto del IGV
                
                // Redondear a 2 decimales para valores monetarios
                subtotalSinIgv = Math.round(subtotalSinIgv * 100.0) / 100.0;
                montoIgv = Math.round(montoIgv * 100.0) / 100.0;
                totalConIgv = Math.round(totalConIgv * 100.0) / 100.0;
                
                // Actualizar los valores para el frontend
                o.setSubtotal(subtotalSinIgv); // Precio sin IGV
                o.setIgv(0.18); // IGV siempre 18% (valor estático para el frontend)
                o.setTotal(totalConIgv); // Total con IGV (precio real del producto)
            });
            response.setDato(compras);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ProductoResponse getProducto(Integer productoId) {
        return integrationService.getProducto(productoId);
    }

    private String getMetodoPago(Integer metodoId) {
        return integrationService.getMetodoPago(metodoId);
    }

    private void updateStock(Integer productoId, Integer cantidadComprada){
        integrationService.updateStock(productoId, cantidadComprada);
    }

}
