package com.example.cpayments.sipay.domain;

public enum PaymentStatus {
    REQUIRES_PAYMENT_METHOD, // Ödeme yöntemi (kart bilgisi) bekleniyor
    REQUIRES_ACTION,         // Müşterinin bir eylem yapması gerekiyor (örn: 3D Secure)
    PROCESSING,              // Ödeme işleniyor
    SUCCEEDED,               // Ödeme başarılı
    FAILED                   // Ödeme başarısız
}