package pe.com.farmaciadey.compra.models.responses;

import lombok.Data;

@Data
public class CarritoResponse {
    private Integer id;
    private Integer cantidad;
    private ProductoResponse producto;
}
