package com.game.chess.Security;

import com.game.chess.Model.AuthRequest;
import com.game.chess.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final int MIN_SECRET_LENGTH = 64;

    private final SecretKey secretKey;

    private final long expirationTime = 24 * 60 * 60 * 1000;   // 24 hours

    public JwtService(@Value("${jwt.secret}") String secret) {

        // trim: a space at the end of the line in application.properties is part of the value
        String cleaned = secret == null ? "" : secret.trim();

        if (cleaned.length() < MIN_SECRET_LENGTH) {
            // we print the LENGTH only, never the secret itself
            throw new IllegalArgumentException(
                    "jwt.secret must contain at least " + MIN_SECRET_LENGTH
                            + " characters, but it has " + cleaned.length());
        }

        this.secretKey = Keys.hmacShaKeyFor(cleaned.getBytes(StandardCharsets.UTF_8));
    }

    /** Used by the login code that works with AuthRequest. The token subject is the username. */
    public String generateToken(AuthRequest user) {
        return buildToken(user.username());
    }

    /** Same token for a User entity (also uses the username as the subject). */
    public String generateToken(User user) {
        return buildToken(user.getUserName());
    }

    /** Kept under its old name because JwtAuthenticationFilter calls it. It returns the token subject (the username). */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /** Clearer name for the same value. */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ helpers

    private String buildToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}