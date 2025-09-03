package pe.com.farmaciadey.producto.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Producto {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(unique = true, nullable = false, length = 45)
    private String codigo;
    @Column(nullable = false, length = 70)
    private String nombre;
    @Column(length = 200)
    private String descripcion;
    private Double precio;
    private Integer stock;
    @Column(length = 200)
    private String url;
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    private Integer eliminado = 0;
}
