package com.marketplace.order.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_STRING = "MySecretKeyThatIsVeryLongAndSecureEnoughForHS256AndLab4";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }
    public String extractRole(String token) { return extractAllClaims(token).get("role", String.class); }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { return claimsResolver.apply(extractAllClaims(token)); }
    private Claims extractAllClaims(String token) { return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody(); }
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", java.util.List.class));
    }
    public Boolean isTokenValid(String token) {
        try { return !extractClaim(token, Claims::getExpiration).before(new Date()); } catch (Exception e) { return false; }
    }
}
