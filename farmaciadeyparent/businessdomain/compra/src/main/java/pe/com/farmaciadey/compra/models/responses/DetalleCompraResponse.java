package pe.com.farmaciadey.compra.models.responses;

import lombok.Data;

@Data
public class DetalleCompraResponse {
    private ProductoResponse producto;
    private Integer cantidad;
    private Double precio;
    private Double subtotal;
}
