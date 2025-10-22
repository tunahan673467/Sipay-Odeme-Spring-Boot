package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.RefreshToken;
import com.example.cpayments.sipay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Token string'ine göre RefreshToken nesnesini bulmak için.
    Optional<RefreshToken> findByToken(String token);

    // Belirli bir kullanıcıya ait RefreshToken'ı bulmak için.
    Optional<RefreshToken> findByUser(User user);

    // Belirli bir kullanıcıya ait RefreshToken'ı silmek için (eskisini temizlerken kullanacağız).
    // Spring Data JPA, 'deleteBy' + AlanAdı kuralıyla otomatik silme metodu oluşturur.
    void deleteByUser(User user);
}