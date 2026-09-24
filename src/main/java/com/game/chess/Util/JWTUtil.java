package com.game.chess.Util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {
    private final String SECRET = "eX9+Z1P2qLmN4vRw8kY3tA6sD0fG1hJ5kL7mNpQrStUvWxYz0aBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUvWw==";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final int expireAT = 1000*60*60;

    public String generateToken(String username){
         return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireAT))
                .signWith(key)
                .compact();

    }
}
