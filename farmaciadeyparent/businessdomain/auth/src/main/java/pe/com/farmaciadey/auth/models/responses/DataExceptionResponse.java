package pe.com.farmaciadey.auth.models.responses;

import lombok.Data;

@Data
public class DataExceptionResponse {
    private String mensaje;
    private Integer estado;
}
