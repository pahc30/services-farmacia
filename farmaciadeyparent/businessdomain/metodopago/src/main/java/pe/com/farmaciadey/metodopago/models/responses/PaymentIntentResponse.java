package pe.com.farmaciadey.metodopago.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {
    
    private boolean success;
    
    private Long transaccionId;
    
    private String clientSecret;
    
    private String stripePaymentIntentId;
    
    private String message;
    
    private String error;
}