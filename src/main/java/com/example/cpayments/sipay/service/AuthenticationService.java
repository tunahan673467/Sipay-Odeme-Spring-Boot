package com.example.cpayments.sipay.service;

import com.example.cpayments.sipay.domain.RefreshToken; // Ekledik
import com.example.cpayments.sipay.domain.User;
import com.example.cpayments.sipay.exception.TokenRefreshException; // Ekledik
import com.example.cpayments.sipay.repository.UserRepository;
import com.example.cpayments.sipay.security.JwtService;
import com.example.cpayments.sipay.web.dto.AuthenticationResponse;
import com.example.cpayments.sipay.web.dto.LoginRequest;
import com.example.cpayments.sipay.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService; // YENİ: RefreshTokenService'i enjekte ettik

    /**
     * Yeni bir kullanıcıyı kaydeder, veritabanına ekler ve token oluşturur.
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles("USER"); // TODO: Rolleri dinamik yap

        userRepository.save(user);

        UserDetails userDetails = buildUserDetails(user); // Yardımcı metoda taşıdık

        // Access token'ı JWT Service ile oluştur
        var jwtToken = jwtService.generateToken(userDetails);
        // Refresh token'ı RefreshTokenService ile oluştur ve DB'ye kaydet
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken()) // DB'den gelen token string'ini kullan
                .build();
    }

    /**
     * Mevcut bir kullanıcının giriş yapmasını sağlar ve token oluşturur.
     */
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after successful auth"));

        UserDetails userDetails = buildUserDetails(user); // Yardımcı metoda taşıdık

        // Access token'ı JWT Service ile oluştur
        var jwtToken = jwtService.generateToken(userDetails);
        // Refresh token'ı RefreshTokenService ile oluştur/güncelle ve DB'ye kaydet
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken()) // DB'den gelen token string'ini kullan
                .build();
    }

    /**
     * Verilen refresh token'ı kullanarak yeni bir access token üretir.
     * Refresh Token Rotation uygular (eski refresh token'ı geçersiz kılar, yenisini üretir).
     */
    public AuthenticationResponse refreshToken(String refreshTokenString) {
        // 1. Gelen refresh token string'ini kullanarak veritabanındaki RefreshToken nesnesini bul.
        //    Bulamazsa veya revoked ise hata verecek.
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new TokenRefreshException(refreshTokenString, "Refresh token not found in database!"));

        // 2. Token'ın süresinin dolup dolmadığını kontrol et.
        //    Süresi dolmuşsa DB'den silinir ve TokenRefreshException fırlatılır.
        refreshTokenService.verifyExpiration(refreshToken);

        // 3. Token geçerliyse, ilişkili kullanıcıyı al.
        User user = refreshToken.getUser();
        UserDetails userDetails = buildUserDetails(user); // Yardımcı metoda taşıdık

        // 4. Yeni bir Access Token oluştur.
        String newAccessToken = jwtService.generateToken(userDetails);

        // 5. YENİ bir Refresh Token oluştur (Rotation!).
        //    refreshTokenService.createRefreshToken metodu zaten eski token'ı silip yenisini DB'ye kaydeder.
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // 6. Yeni token çiftini dön.
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }


    // --- Yardımcı Metot ---
    /**
     * User entity'sinden Spring Security'nin anlayacağı UserDetails nesnesini oluşturur.
     * (Bu kısım ileride User sınıfının UserDetails arayüzünü implemente etmesiyle daha da iyileştirilebilir)
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER") // TODO: Rolleri user.getRoles() ile dinamik yap
                .build();
    }
}