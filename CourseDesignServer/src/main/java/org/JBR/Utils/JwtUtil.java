package org.JBR.Utils;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;

public class JwtUtil {
    
    private static final String SECRET_STRING = "mySecretKeyMustBeAtLeast32BytesLong123456";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    private static final long EXPIRATION_TIME = 86400000; //* 24 hours in milliseconds

    public static String generateToken(String user_id, String role) {
        return Jwts.builder()
                .subject(user_id)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)  //* Use SecretKey
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static Map<String, String> extractUserInfo(String BearerToken) {
        String cleanedToken = BearerToken.replaceFirst("^Bearer\\s+", "");
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(cleanedToken)
                .getPayload();
        
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("user_id", claims.getSubject());
        userInfo.put("role", claims.get("role", String.class));
        
        return userInfo;
    }
}