package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu işlemin hangi ödeme niyetine ait olduğunu belirtir.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_intent_id", nullable = false)
    private PaymentIntent paymentIntent;

    // İşlem tipi (SALE, REFUND vb.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    // Bu işleme ait tutar. (Örneğin iade, tam tutardan daha az olabilir)
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    // Bu işlemin durumu (Başarılı, Başarısız vb. - Basitlik için şimdilik String)
    @Column(nullable = false)
    private String status;

    // --- Ödeme Sağlayıcıdan Dönen Bilgiler ---
    private String rrn; // Retrieval Reference Number
    private String authCode; // Otorizasyon Kodu
    private String externalTxId; // Ödeme sağlayıcının bu işleme verdiği benzersiz ID

    // Ödeme sağlayıcıdan gelen tüm cevabı ham olarak (JSON formatında) saklamak için.
    // Hata ayıklama ve kayıt tutma için çok önemlidir.
    @Column(columnDefinition = "jsonb")
    private String rawResponse;

    // --- Denetim Alanları ---
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}