package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    // Şimdilik standart JpaRepository metotları (save, findById vb.) yeterli.
    // İleride ihtiyaç duyarsak buraya özel metotlar ekleyebiliriz.
    // Örneğin: Optional<Merchant> findByApiKeyMasked(String apiKeyMasked);
}