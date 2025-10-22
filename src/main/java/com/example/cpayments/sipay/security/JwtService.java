package com.example.cpayments.sipay.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // Düzeltme 1: java.security.Key yerine javax.crypto.SecretKey
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshExpiration;

    // --- 1. Token Oluşturma ---

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // Düzeltme 2: .signWith(getSignInKey(), SignatureAlgorithm.HS256) yerine
                // Sadece anahtarı vermek yeterli, anahtar zaten algoritmayı biliyor.
                .signWith(getSignInKey())
                .compact();
    }


    // --- 2. Token Doğrulama ve Okuma ---

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Gelen token'ı ve gizli anahtarımızı kullanarak token'ın içindeki tüm
    // bilgi parçalarını (claims) "çözer".
    private Claims extractAllClaims(String token) {
        // Düzeltme 3: Burası jjwt 0.12.5 versiyonu için tamamen güncellendi.
        return Jwts
                .parser() // .parserBuilder() DEĞİL
                .verifyWith(getSignInKey()) // .setSigningKey() DEĞİL
                .build()
                .parseSignedClaims(token) // .parseClaimsJws() DEĞİL
                .getPayload(); // .getBody() DEĞİL
    }


    // --- 3. Gizli Anahtarı Hazırlama ---

    // Düzeltme 4: Dönüş tipi 'Key' değil, 'SecretKey' olmalı.
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}