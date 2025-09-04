package com.example.platform.util;


import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "jC98qMCydqN1VfM0xgS9DQ1+oOaUYgAq1eCv5wlNc1A=";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10* 10; // 10 saat

    // Token üretimi
    public String generateTokenWithApiKey(UUID apiKey, Platform platformTitle, Date issuedAt, Date expiresAt,List<String> role) {
        return Jwts.builder()
                .setSubject(apiKey.toString())// apikey UUID → string olarak yazılıyor
                .claim("platformTitle",platformTitle.getPlatformTitle())
                .claim("platformCode",platformTitle.getPlatformCode())
                .claim("platformId",platformTitle.getId())
                .claim("role",role)
                .setIssuedAt(new Date())       // oluşturulma zamanı
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // geçerlilik süresi
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // imzalama
                .compact();                    // token string'ini üret
    }

    public String generateToken(String username, List<String> role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public String generateTokenReact(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }




    // Token geçerlilik kontrolü (içeride kullanılabilir)
    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    //Token'dan tüm claim'leri çıkar
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    //İstenilen claim’i dışarıdan fonksiyonla çek
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Long extractPlatformId(String token) {
        return extractClaim(token, claims -> {
            Integer id = claims.get("platformId", Integer.class);
            return id != null ? id.longValue() : null;
        });
    }
    public String extractMernis(String token) {
        return extractClaim(token, claims -> claims.get("mernis", String.class));
    }



}
