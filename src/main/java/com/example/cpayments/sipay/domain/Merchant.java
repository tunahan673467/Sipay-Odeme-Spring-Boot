package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "merchants")
@Getter
@Setter
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Gerçek API Key'i ASLA burada saklamayız.
    // Bu alan, anahtarın bir kısmını veya bir referansını tutmak içindir.
    // Örnek: "key_..._a1b2"
    @Column(nullable = false)
    private String apiKeyMasked;

    // Gerçek API Secret'ı ASLA burada saklamayız.
    // Bu alan, secret'ın HASH'lenmiş halini veya bir referansını tutar.
    @Column(nullable = false)
    private String apiSecretHashed; // Veya apiSecretMasked

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}