package pe.com.farmaciadey.compra.models.responses;

import java.util.Date;
import java.util.List;

import lombok.Data;

 @Data
public class CompraResponse {
    private Integer id;
    private String codigo;
    private Date fecha;
    private String metodoPago;
    private Double igv = 0.18;
    private Double subtotal;
    private Double total;
    private List<DetalleCompraResponse> detalleCompra;

}
