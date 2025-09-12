package com.example.platform.util;


import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    // Token üretimi
    public String generateTokenWithApiKey(UUID apiKey, Platform platformTitle, Date issuedAt, Date expiresAt,List<String> role) {
        return Jwts.builder()
                .setSubject(apiKey.toString())// apikey UUID → string olarak yazılıyor
                .claim("platformTitle",platformTitle.getPlatformTitle())
                .claim("platformCode",platformTitle.getPlatformCode())
                .claim("platformId",platformTitle.getId())
                .claim("role",role)
                .setIssuedAt(new Date())       // oluşturulma zamanı
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // geçerlilik süresi
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // imzalama
                .compact();                    // token string'ini üret
    }

    public String generateToken(String username, List<String> role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String generateTokenReact(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
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
                .setSigningKey(secretKey.getBytes())
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
