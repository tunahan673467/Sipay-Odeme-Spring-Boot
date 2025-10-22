package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    // Şimdilik standart metotlar yeterli.
}