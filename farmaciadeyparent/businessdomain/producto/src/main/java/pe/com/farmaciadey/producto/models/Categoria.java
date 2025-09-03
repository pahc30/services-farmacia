package pe.com.farmaciadey.producto.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Categoria {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(nullable = false, length = 45)
    private String nombre;
    @Column(length = 200)
    private String descripcion;
    private Integer eliminado = 0;
}
