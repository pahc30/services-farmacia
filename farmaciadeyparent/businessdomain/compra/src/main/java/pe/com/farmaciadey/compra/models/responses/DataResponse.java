package pe.com.farmaciadey.compra.models.responses;

import pe.com.farmaciadey.compra.utils.Utils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse implements java.io.Serializable {
    @JsonProperty("dato")
    private Object dato;
    @JsonProperty("mensaje")
    private String mensaje;
    @JsonProperty("estado")
    private Integer estado;

    public Object getDato() { return dato; }
    public void setDato(Object dato) { this.dato = dato; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

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
