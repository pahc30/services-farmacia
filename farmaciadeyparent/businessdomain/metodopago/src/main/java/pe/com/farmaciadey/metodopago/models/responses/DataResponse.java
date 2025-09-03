package pe.com.farmaciadey.metodopago.models.responses;

import pe.com.farmaciadey.metodopago.utils.Utils;

import lombok.Data;

@Data
public class DataResponse {
    private Object dato;
    private String mensaje;
    private Integer estado;

    public DataResponse() {
        this.estado = Utils.REQUEST_OK;
    }

    public DataResponse(Object dato) {
        this.dato = dato;
        this.estado = Utils.REQUEST_OK;
    }

    public DataResponse(Object dato, Integer estado) {
        this.dato = dato;
        this.estado = estado;
    }

    public DataResponse(String mensaje) {
        this.mensaje = mensaje;
        this.estado = Utils.REQUEST_OK;
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

    public void setException(String mensaje) {
        this.mensaje = "Error interno. " + mensaje;
        this.dato = null;
        this.estado = Utils.REQUEST_ERROR;
    }
}
