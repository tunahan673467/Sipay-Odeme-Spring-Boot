package com.example.cpayments.sipay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Bu anotasyon, bu hata fırlatıldığında Spring'in otomatik olarak
// HTTP 403 Forbidden cevabı dönmesini sağlar.
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

    // Java'nın standart hata sınıflarını genişletiyoruz.
    // Serialization için gerekli.
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}