package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder // Builder pattern'ını kullanabilmek için
@NoArgsConstructor // Lombok için boş constructor
@AllArgsConstructor // Lombok için tüm alanları içeren constructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu token'ın hangi kullanıcıya ait olduğunu belirtir.
    // OneToOne: Her kullanıcının genellikle sadece bir aktif refresh token'ı olur.
    // CascadeType.ALL: User silinirse ilişkili RefreshToken da silinsin.
    // orphanRemoval = true: User üzerinden RefreshToken kaldırılırsa DB'den silinsin.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // Token'ın kendisi (uzun bir String olacak). Benzersiz olmalı.
    @Column(nullable = false, unique = true, length = 1024) // Token uzun olabilir, length artıralım.
    private String token;

    // Token'ın son kullanma tarihi.
    @Column(nullable = false)
    private Instant expiryDate;

    // (İsteğe bağlı ama önerilen) Token'ın geçersiz kılınıp kılınmadığını belirtir.
    // Silmek yerine 'revoked = true' yapmak, geçmişi takip etmek için daha iyi olabilir.
    private boolean revoked = false; // Varsayılan olarak geçerli

}