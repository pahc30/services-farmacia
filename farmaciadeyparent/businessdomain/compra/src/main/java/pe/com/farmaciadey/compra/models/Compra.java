package pe.com.farmaciadey.compra.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Compra {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(unique = true, nullable = false, length = 200)
    private String codigo;
    private Date fecha;
    private Integer usuarioId;
    private Integer metodoPagoId;
    private Double igv = 0.18;
    private Double subtotal;
    private Double total;
    private Integer eliminado = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleCompra> detalleCompra;

    @Transient
    private String metodoPago;
}
