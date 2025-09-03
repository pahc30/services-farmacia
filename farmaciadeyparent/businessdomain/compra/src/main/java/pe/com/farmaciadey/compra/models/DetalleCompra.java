package pe.com.farmaciadey.compra.models;

import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class DetalleCompra {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "compraId", nullable = false)
    @JsonBackReference
    private Compra compra;
    private Integer productoId;
    private Integer carritoCompraId;
    private Integer cantidad;
    private Double precio;
    private Double subtotal;
    private Integer eliminado = 0;    

    @Transient
    private ProductoResponse producto;
    
}
