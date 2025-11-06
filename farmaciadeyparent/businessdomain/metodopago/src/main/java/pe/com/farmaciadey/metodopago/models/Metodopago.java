package pe.com.farmaciadey.metodopago.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Metodopago", schema = "metodopago_schema")
public class Metodopago {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(nullable=false, length = 45)
    private String tipo;
    @Column(length = 250)
    private String descripcion;
    @Column(nullable=false)
    private Integer eliminado = 0;
}
