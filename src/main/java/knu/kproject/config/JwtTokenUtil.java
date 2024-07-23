package knu.kproject.config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

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


    public String createAccessToken(String socialId) {
        return createToken(socialId, ACCESS_TOKEN_EXPIRY_TIME);
    }

    public String createRefreshToken(String socialId) {
        return createToken(socialId, REFRESH_TOKEN_EXPIRY_TIME);
    }

    private String createToken(String socialId, long expiryTime) {
        return Jwts.builder()
                .setSubject(socialId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSocialId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String socialId) {
        final String extractedSocialId = extractSocialId(token);
        return (extractedSocialId.equals(socialId) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}