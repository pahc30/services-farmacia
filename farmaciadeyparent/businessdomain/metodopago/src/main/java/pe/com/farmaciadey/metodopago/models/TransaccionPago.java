package pe.com.farmaciadey.metodopago.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaccion_pago", schema = "metodopago_schema")
public class TransaccionPago {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    @Column(nullable = false)
    private Long compraId;
    
    @Column(nullable = false)
    private Long metodoPagoId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    /**
     * Moneda de la transacción (ej: USD, PEN)
     */
    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda = "USD";
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Fecha de la última actualización
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Fecha en que se completó el pago
     */
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(length = 200)
    private String referenciaExterna; // ID de transacción de Stripe
    
    /**
     * Client secret para completar el pago en el frontend
     */
    @Column(name = "client_secret", length = 200)
    private String clientSecret;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(length = 1000)
    private String detallesRespuesta; // JSON response de Stripe
    
    @Column(length = 500)
    private String mensajeError;
    
    @Column(nullable = false)
    private Integer eliminado = 0;
    
    public enum EstadoTransaccion {
        PENDIENTE,
        PROCESANDO,
        COMPLETADA,
        FALLIDA,
        CANCELADA,
        REEMBOLSADA
    }

    /**
     * Método para actualizar automáticamente la fecha de modificación
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Método para establecer la fecha de creación antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        this.fechaActualizacion = LocalDateTime.now();
    }
}