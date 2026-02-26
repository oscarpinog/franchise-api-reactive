package com.franchise.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    /**
     * Se usa la sintaxis ${VARIABLE:VALOR_POR_DEFECTO}
     * Si Docker inyecta JWT_SECRET, se usará ese valor. 
     * Si no (en el IDE), usará la cadena de texto de respaldo.
     */
    @Value("${JWT_SECRET:msc_oscar_rodriguez_ingeniero_de_software_default_secure_key_32_characters}")
    private String secret;

    private static final long EXPIRATION_TIME = 3600000; // 1 hora
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Inicializamos la llave una vez que Spring ha inyectado el valor del secret
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            // Si el token está mal formado o expirado, parseClaimsJws lanzará una excepción
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}