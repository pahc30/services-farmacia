package pe.com.farmaciadey.auth.models.responses;

import lombok.Data;

@Data
public class DataResponse {
    private Object dato;
    private String mensaje;
    private Integer estado;

    public DataResponse() {
    }

    public DataResponse(Object dato) {
        this.dato = dato;
    }

    public DataResponse(Object dato, Integer estado) {
        this.dato = dato;
        this.estado = estado;
    }

    public DataResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public DataResponse(String mensaje, Integer estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }

    public DataResponse(Object dato, String mensaje, Integer estado) {
        this.dato = dato;
        this.mensaje = mensaje;
        this.estado = estado;
    }
}
