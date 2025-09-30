package pe.com.farmaciadey.compra.models.responses;

import lombok.Data;

@Data
public class CarritoResponse implements java.io.Serializable {
    private Integer id;
    private Integer cantidad;
    private ProductoResponse producto;
}
