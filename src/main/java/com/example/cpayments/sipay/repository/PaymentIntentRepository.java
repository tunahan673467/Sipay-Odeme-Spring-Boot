package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    // Idempotency key'e göre ödeme niyetini bulmak için.
    Optional<PaymentIntent> findByIdempotencyKey(String idempotencyKey);
}