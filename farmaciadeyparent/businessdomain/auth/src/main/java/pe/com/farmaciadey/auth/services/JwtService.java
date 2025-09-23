package pe.com.farmaciadey.auth.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import pe.com.farmaciadey.auth.constants.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private SecretKey getSignKey() {
        // Utils.secretKey debe ser BASE64 (32 bytes mínimo para HS256)
        byte[] keyBytes = Decoders.BASE64.decode(Utils.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String GenerateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user", username);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + Utils.jwtExpirationTime);

        return Jwts.builder()
                // En 0.12.x puedes usar los setters “nuevos”:
                .claims(claims != null ? claims : Map.of())
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                // Importante: firma nueva con Jwts.SIG.HS256
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        // 0.12.x: parser() -> verifyWith(key) -> build() -> parseSignedClaims(token)
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
