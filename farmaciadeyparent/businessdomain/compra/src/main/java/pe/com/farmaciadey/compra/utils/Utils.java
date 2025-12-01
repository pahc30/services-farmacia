package pe.com.farmaciadey.compra.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    public final static Integer REQUEST_OK = 1;
    public final static Integer REQUEST_ERROR = 0;
    
    public static String BASE_URL_PRODUCTO;
    public static String BASE_URL_METODO_PAGO;
    
    @Value("${service.producto.url:http://farmacia-producto:7013/producto}")
    public void setBaseUrlProducto(String url) {
        BASE_URL_PRODUCTO = url;
    }
    
    @Value("${service.metodopago.url:http://farmacia-metodopago:7014/metodopago}")
    public void setBaseUrlMetodoPago(String url) {
        BASE_URL_METODO_PAGO = url;
    }
}
