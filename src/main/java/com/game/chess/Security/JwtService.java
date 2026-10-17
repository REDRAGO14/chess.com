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

    private final String SECRET = "eX9+Z1P2qLmN4vRw8kY3tA6sD0fG1hJ5kL7mNpQrStUvWxYz0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUvWw==";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());


    private final long expirationTime = 24 * 60 * 60 * 1000;



    public String generateToken(AuthRequest user) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + expirationTime
        );

        return Jwts.builder()
                .subject(user.username())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }


    public String extractEmail(String token) {

        Claims claims = getClaims(token);

        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    public boolean isTokenValid(String token) {
        return getClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
    }
}