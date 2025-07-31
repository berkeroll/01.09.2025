package com.example.platform.util;


import com.example.platform.model.Platform;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "jC98qMCydqN1VfM0xgS9DQ1+oOaUYgAq1eCv5wlNc1A=";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 saat

    // Token üretimi
    public String generateTokenWithApiKey(UUID apiKey, Platform platformTitle, Date issuedAt, Date expiresAt) {
        return Jwts.builder()
                .setSubject(apiKey.toString())
                .claim("platformTitle",platformTitle.getPlatformTitle())
                .claim("platformCode",platformTitle.getPlatformCode())// apikey UUID → string olarak yazılıyor
                .setIssuedAt(new Date())       // oluşturulma zamanı
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // geçerlilik süresi
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // imzalama
                .compact();                    // token string'ini üret
    }



    // Token geçerlilik kontrolü (içeride kullanılabilir)
    private boolean isTokenExpired(String token) {
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
}
