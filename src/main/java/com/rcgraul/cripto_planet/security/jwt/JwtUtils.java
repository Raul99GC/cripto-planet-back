package com.rcgraul.cripto_planet.security.jwt;

import com.rcgraul.cripto_planet.enums.JwtType;
import com.rcgraul.cripto_planet.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtSecretRefresh}")
    private String jwtSecretRefresh;

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

    private SecretKey keyRefresh() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretRefresh));
    }

    private Claims getClaimsFromToken(String token, JwtType type) {

        if (type == JwtType.REFRESH_TOKEN) {
            return Jwts.parser()
                    .verifyWith(keyRefresh())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } else if (type == JwtType.SIGNIN_TOKEN) {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        return null;
    }

    public String getUsernameFromToken(String token, JwtType type) {
        return String.valueOf(Objects.requireNonNull(getClaimsFromToken(token, type)).get("sub"));
    }

    public String getAuthorities(String token, JwtType type) {
        return String.valueOf(Objects.requireNonNull(getClaimsFromToken(token, type)).get("authorities"));
    }

    public String generateTokenFromUserDetails(UserDetailsImpl userDetails) {

        String email = userDetails.getEmail();
        String username = userDetails.getUsername();
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtBuilder token = Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + 7200000))) // 2 hrs
                .signWith(key());

        if (email != null) token.claim("email", email);

        return token.compact();
    }

    public String generateRefreshToken(UserDetailsImpl userDetails) {
        String email = userDetails.getEmail();
        String username = userDetails.getUsername();
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtBuilder token = Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + 86400000))) // 24 hrs
                .signWith(keyRefresh());

        if (email != null) token.claim("email", email);

        return token.compact();

    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty", e);
        }
    }

    public boolean validateRefreshToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) keyRefresh()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty", e);
        }
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        if (!validateRefreshToken(refreshToken)) throw new IllegalArgumentException("Invalid Refresh Token");

        JwtBuilder token = Jwts.builder()
                .subject(getUsernameFromToken(refreshToken, JwtType.REFRESH_TOKEN))
                .claim("authorities", getAuthorities(refreshToken, JwtType.REFRESH_TOKEN))
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + 7200000))) // 2 hrs
                .signWith(key());

        return token.compact();
    }
}
