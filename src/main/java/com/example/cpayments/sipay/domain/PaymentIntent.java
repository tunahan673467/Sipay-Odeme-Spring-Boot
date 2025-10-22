package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_intents", uniqueConstraints = {
        // Idempotency key'in benzersiz olmasını sağlıyoruz.
        @UniqueConstraint(columnNames = "idempotencyKey")
})
@Getter
@Setter
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- İlişkiler (Relationships) ---
    // @ManyToOne: "Çok sayıda PaymentIntent bir Merchant'a ait olabilir" ilişkisi.
    // @JoinColumn: Bu ilişkiyi kuracak olan foreign key sütununun adını belirtir.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // --- Ödeme Detayları ---
    // Para birimleri için 'double' veya 'float' kullanmak yuvarlama hatalarına
    // yol açar. 'BigDecimal' en doğru ve güvenli yoldur.
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    // Enum tipindeki durumları veritabanında String olarak saklamak için.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private boolean requires3ds;

    // Tekrarlanan istekleri engellemek için istemci tarafından gönderilen benzersiz anahtar.
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    // 3D Secure sonrası müşterinin yönlendirileceği URL.
    @Column(length = 2048)
    private String returnUrl;

    // --- Denetim Alanları ---
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}