package pe.com.farmaciadey.auth.constants;

public class Utils {
    private Utils() {}
    public final static String secretKey = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629";
    public final static Integer jwtExpirationTime = 60 * 60 * 1000 * 6;

    public final static Integer REQUEST_OK = 1;
    public final static Integer REQUEST_ERROR = 0;
}
