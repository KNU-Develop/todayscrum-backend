package knu.kproject.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import knu.kproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access-token-expiration}")
    private long ACCESS_TOKEN_EXPIRY_TIME;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRY_TIME;

    private Key key;

    @PostConstruct
    public void inti() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String createAccessToken(String userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRY_TIME);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRY_TIME);
    }

    private String createToken(String userId, long expiryTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusMinutes(expiryTime);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(DateUtil.toDate(now))
                .setExpiration(DateUtil.toDate(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String userId) {
        final String extractedUserId = extractUserId(token);
        return (extractedUserId.equals(userId) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(DateUtil.toDate(LocalDateTime.now()));
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}