package pe.com.farmaciadey.auth.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {
    @Test
    void testValidateTokenWithNullToken() {
        UserDetails userDetails = User.withUsername("testuser").password("password").roles("USER").build();
        assertFalse(jwtService.validateToken(null, userDetails));
    }
        @Test
        void testValidateTokenWithNullSubject() throws Exception {
            // Token sin subject
            java.lang.reflect.Method getSignKey = JwtService.class.getDeclaredMethod("getSignKey");
            getSignKey.setAccessible(true);
            Object key = getSignKey.invoke(jwtService);
            String token = io.jsonwebtoken.Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith((javax.crypto.SecretKey) key, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
            UserDetails userDetails = User.withUsername("testuser").password("password").roles("USER").build();
            assertFalse(jwtService.validateToken(token, userDetails));
        }

        @Test
        void testValidateTokenWithNullUserDetailsUsername() {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn(null);
            String token = jwtService.GenerateToken("testuser");
            assertFalse(jwtService.validateToken(token, userDetails));
        }

        @Test
        void testCreateTokenWithEmptyClaimsAndNullUsername() throws Exception {
            java.lang.reflect.Method method = JwtService.class.getDeclaredMethod("createToken", Map.class, String.class);
            method.setAccessible(true);
            String token = (String) method.invoke(jwtService, Map.of(), null);
            assertNotNull(token);
        }

        @Test
        void testExtractAllClaimsWithInvalidTokenThrows() {
            assertThrows(Exception.class, () -> jwtService.extractAllClaims("invalid.token"));
        }

    @Test
    void testValidateTokenWithEmptyToken() {
        UserDetails userDetails = User.withUsername("testuser").password("password").roles("USER").build();
        assertFalse(jwtService.validateToken("", userDetails));
    }

    @Test
    void testValidateTokenWithInvalidSignature() throws Exception {
        String username = "testuser";
        Date now = new Date();
        Date exp = new Date(now.getTime() + 10000);
        // Crear una clave diferente para la firma
        javax.crypto.SecretKey fakeKey = javax.crypto.KeyGenerator.getInstance("HmacSHA256").generateKey();
        String token = io.jsonwebtoken.Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(fakeKey, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
        UserDetails userDetails = User.withUsername(username).password("password").roles("USER").build();
        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testExtractClaimWithInvalidToken() {
        Function<Claims, String> resolver = Claims::getSubject;
        assertThrows(Exception.class, () -> jwtService.extractClaim("invalid.token", resolver));
    }
    @Test
    void testValidateTokenWithExpiredToken() {
    String username = "testuser";
    Date now = new Date();
    Date exp = new Date(now.getTime() - 1000); // ya expirado
    try {
        java.lang.reflect.Method getSignKey = JwtService.class.getDeclaredMethod("getSignKey");
        getSignKey.setAccessible(true);
        Object key = getSignKey.invoke(jwtService);
        String token = io.jsonwebtoken.Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(exp)
            .signWith((javax.crypto.SecretKey) key, io.jsonwebtoken.Jwts.SIG.HS256)
            .compact();
        UserDetails userDetails = User.withUsername(username).password("password").roles("USER").build();
        assertFalse(jwtService.validateToken(token, userDetails));
    } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
        fail("No se pudo obtener la clave de firma: " + e.getMessage());
    }
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        String username = "testuser";
        String token = "malformed.token";
        UserDetails userDetails = User.withUsername(username).password("password").roles("USER").build();
        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testCreateTokenWithNullClaims() {
        // Usar el método privado createToken con claims nulos
        try {
            java.lang.reflect.Method method = JwtService.class.getDeclaredMethod("createToken", Map.class, String.class);
            method.setAccessible(true);
            String token = (String) method.invoke(jwtService, null, "testuser");
            assertNotNull(token);
        } catch (Exception e) {
            fail("No se pudo invocar createToken con claims nulos");
        }
    }
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private Function<Claims, String> claimsResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTokenAndValidateToken() {
        String username = "testuser";
        String token = jwtService.GenerateToken(username);
        assertNotNull(token);
        UserDetails userDetails = User.withUsername(username).password("password").roles("USER").build();
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtService.GenerateToken(username);
        String extracted = jwtService.extractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.GenerateToken("testuser");
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractClaim() {
    String token = jwtService.GenerateToken("testuser");
    when(claimsResolver.apply(any(Claims.class))).thenReturn("testuser");
    String result = jwtService.extractClaim(token, claimsResolver);
    assertEquals("testuser", result);
    }

    @Test
    void testExtractAllClaims() {
        String token = jwtService.GenerateToken("testuser");
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
    }
}
