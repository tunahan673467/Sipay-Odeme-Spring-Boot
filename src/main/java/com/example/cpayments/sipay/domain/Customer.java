package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Müşterinin e-posta adresi. Benzersiz (unique) olabilir ama zorunlu değil.
    @Column(unique = true)
    private String email;

    private String phone;

    // Müşteriyi kendi sisteminizde (varsa) takip etmek için kullanabileceğiniz
    // harici bir referans numarası veya ID'si.
    @Column(unique = true)
    private String externalRef;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}