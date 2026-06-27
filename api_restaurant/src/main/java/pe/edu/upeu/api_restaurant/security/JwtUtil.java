package pe.edu.upeu.api_restaurant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracion-ms}")
    private long expiracionMs;

    @PostConstruct
    void validarConfiguracion() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 32 bytes para HS256");
        }
        if (expiracionMs <= 0) {
            throw new IllegalStateException("jwt.expiracion-ms debe ser mayor que cero");
        }
    }

    public String generarToken(String email, String rol, UUID usuarioId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio para generar el token");
        }
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio para generar el token");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuarioId es obligatorio para generar el token");
        }
        return Jwts.builder()
            .subject(email)
            .claim("rol", rol)
            .claim("usuarioId", usuarioId.toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiracionMs))
            .signWith(obtenerClave())
            .compact();
    }

    public String extraerEmail(String token) {
        return parsearToken(token).getSubject();
    }

    public UUID extraerUsuarioId(String token) {
        String usuarioId = parsearToken(token).get("usuarioId", String.class);
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new JwtException("El token no contiene usuarioId");
        }
        return UUID.fromString(usuarioId);
    }

    public boolean esTokenValido(String token, String email) {
        try {
            Claims claims = parsearToken(token);
            String usuarioId = claims.get("usuarioId", String.class);
            return email.equals(claims.getSubject())
                && claims.getExpiration().after(new Date())
                && usuarioId != null
                && !usuarioId.isBlank();
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    Claims parsearToken(String token) {
        return Jwts.parser()
            .verifyWith(obtenerClave())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
