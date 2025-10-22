package com.example.cpayments.sipay.service;

import com.example.cpayments.sipay.domain.RefreshToken;
import com.example.cpayments.sipay.domain.User;
import com.example.cpayments.sipay.exception.TokenRefreshException; // Birazdan oluşturacağız
import com.example.cpayments.sipay.repository.RefreshTokenRepository;
import com.example.cpayments.sipay.repository.UserRepository;
import com.example.cpayments.sipay.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Önemli!

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshExpirationMs; // Süreyi milisaniye olarak alıyoruz

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService; // Refresh token string'ini üretmek için

    /**
     * Verilen token string'ine göre RefreshToken nesnesini bulur.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Belirli bir kullanıcı için yeni bir refresh token oluşturur,
     * eskisini siler ve yenisini veritabanına kaydeder.
     */
    @Transactional
    public RefreshToken createRefreshToken(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        // --- DEĞİŞİKLİK BURADA ---
        // 1. Önce bu kullanıcıya ait var olan token'ı bulalım.
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        // 2. Eğer varsa, onu SİLİP veritabanına değişikliği HEMEN YAZALIM (flush).
        existingTokenOpt.ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            refreshTokenRepository.flush(); // Değişikliğin hemen DB'ye gitmesini zorla
        });
        // --- DEĞİŞİKLİK BİTTİ ---


        // 3. Yeni bir refresh token string'i üret
        var simpleUserDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
        String tokenString = jwtService.generateRefreshToken(simpleUserDetails);

        // 4. Yeni RefreshToken nesnesini oluştur
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        // 5. Yeni token'ı veritabanına kaydet
        //    (Artık eski token'ın silindiğinden %100 eminiz)
        return refreshTokenRepository.save(refreshToken);
    }
    /**
     * Verilen refresh token'ın süresinin dolup dolmadığını kontrol eder.
     * Süresi dolmuşsa veritabanından siler ve hata fırlatır.
     * @return Süresi dolmamışsa token'ın kendisini geri döner.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // Süre dolmuşsa...
            refreshTokenRepository.delete(token); // Veritabanından sil
            // Özel bir hata fırlat (bunu Controller'da yakalayıp kullanıcıya bilgi vereceğiz)
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        // Süre dolmamışsa token'ı geri dön
        return token;
    }

    /**
     * Belirli bir kullanıcı ID'sine ait refresh token'ı siler.
     * (Örn: Kullanıcı şifresini değiştirdiğinde veya "tüm cihazlardan çıkış yap" dediğinde)
     */
    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        refreshTokenRepository.deleteByUser(user);
        // Kaç kaydın silindiğini döndürebiliriz (genellikle 1 veya 0 olur)
        return 1; // Basitleştirilmiş dönüş değeri
    }
}