package pe.com.farmaciadey.metodopago.models.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequest {
    
    @NotNull(message = "La compra ID es obligatoria")
    private Long compraId;
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private Double monto;
    
    @NotBlank(message = "La moneda es obligatoria")
    @Builder.Default
    private String moneda = "usd";
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private String emailComprador;
    
    private String nombreComprador;
}