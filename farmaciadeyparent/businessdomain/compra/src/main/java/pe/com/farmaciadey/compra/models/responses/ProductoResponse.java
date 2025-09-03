package pe.com.farmaciadey.compra.models.responses;

import lombok.Data;

@Data
public class ProductoResponse {
    private Integer id;
    private String codigo;
    private Double precio;
    private Integer stock;
    private String nombre;
    private String url;
    private String categoria;
}
