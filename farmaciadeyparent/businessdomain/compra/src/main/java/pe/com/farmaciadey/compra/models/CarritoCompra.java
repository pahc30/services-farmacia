package pe.com.farmaciadey.compra.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CarritoCompra {
    public CarritoCompra() {}
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer cantidad;
    private Integer usuarioId;
    private Integer productoId;
    private Integer eliminado = 0;
}
