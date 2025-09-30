package pe.com.farmaciadey.compra.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

            request.setCodigo(UUID.randomUUID().toString());
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
                carritoCompraService.delete(x.getCarritoCompraId());
            });

            return new ResponseEntity<>(response, HttpStatus.CREATED);
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
