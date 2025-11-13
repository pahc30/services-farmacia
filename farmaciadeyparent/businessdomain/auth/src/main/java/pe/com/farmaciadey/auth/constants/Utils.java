package pe.com.farmaciadey.auth.constants;

public class Utils {
    private Utils() {}
    
    /**
     * Clave secreta JWT de 512 bits (64 bytes) en hexadecimal
     * Generada específicamente para seguridad en producción
     * ¡NUNCA compartir esta clave!
     */
    public final static String secretKey = "4A614E645267556B58703273357538782F413F4428472B4B6250655368566D597133743677397A24432646294A614E645267556B58703273357538782F413F442847";
    
    /**
     * Tiempo de expiración del JWT: 6 horas (en milisegundos)
     */
    public final static Integer jwtExpirationTime = 60 * 60 * 1000 * 6;

    public final static Integer REQUEST_OK = 1;
    public final static Integer REQUEST_ERROR = 0;
}
