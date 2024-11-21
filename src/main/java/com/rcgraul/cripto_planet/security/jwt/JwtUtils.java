package com.rcgraul.cripto_planet.security.jwt;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.rcgraul.cripto_planet.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtHeader}")
    private String jwtHeader;

    public String getJwtFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(jwtHeader);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private Claims getClaimsFromToken(String token) {

        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token) {

        return String.valueOf(getClaimsFromToken(token).get("email"));
    }

    public String getAuthorities(String token) {

        return String.valueOf(getClaimsFromToken(token).get("authorities"));
    }

    public String generateTokenFromEmail(UserDetailsImpl userDetails) {

        String email = userDetails.getUsername();
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(email)
                .claim("email", email)
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + 86400000)))
                .signWith(key())
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty", e);
        }
        return false;
    }

}
