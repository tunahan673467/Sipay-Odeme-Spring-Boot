package com.example.cpayments.sipay.web;

import com.example.cpayments.sipay.service.AuthenticationService;
import com.example.cpayments.sipay.web.dto.AuthenticationResponse;
import com.example.cpayments.sipay.web.dto.LoginRequest;
import com.example.cpayments.sipay.web.dto.RefreshTokenRequest; // Eksik import eklendi
import com.example.cpayments.sipay.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * POST /api/auth/register
     * Yeni bir kullanıcı kaydı oluşturur.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/login
     * Mevcut bir kullanıcıyı doğrular ve yeni token'lar döner.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/refresh
     * Verilen geçerli bir refresh token kullanarak yeni bir access token
     * ve (Rotation nedeniyle) yeni bir refresh token döner.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        String refreshTokenString = request.getRefreshToken();
        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenString);
        return ResponseEntity.ok(response);
    }
}