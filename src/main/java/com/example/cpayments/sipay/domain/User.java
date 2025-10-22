package com.example.cpayments.sipay.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

// --- JPA Anotasyonları ---

// Bu sınıfın bir veritabanı tablosuna karşılık geldiğini belirtir.
@Entity
// Veritabanında oluşacak tablonun adını 'users' olarak belirler.
// Ayrıca email alanının benzersiz (unique) olması için bir kısıtlama ekler.
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})

// --- Lombok Anotasyonları (Tekrar eden kodları azaltmak için) ---
@Getter
@Setter
public class User {

    // @Id: Bu alanın tablonun birincil anahtarı (primary key) olduğunu belirtir.
    // @GeneratedValue: Bu alanın değerinin veritabanı tarafından otomatik olarak
    // (örneğin her yeni kayıtta artarak) oluşturulacağını belirtir.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column: Bu alanın bir sütuna karşılık geldiğini belirtir.
    // nullable = false: Bu sütunun boş bırakılamayacağını (zorunlu alan) belirtir.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // TODO: Rol yönetimi daha sonra eklenecek, şimdilik basit bir String.
    private String roles;

    // --- Denetim (Auditing) Alanları ---

    // @CreationTimestamp: Bu alana, yeni bir kayıt oluşturulduğunda o anki
    // zaman damgasının otomatik olarak atanmasını sağlar.
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // @UpdateTimestamp: Bu alana, bir kayıt her güncellendiğinde o anki
    // zaman damgasının otomatik olarak atanmasını sağlar.
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}