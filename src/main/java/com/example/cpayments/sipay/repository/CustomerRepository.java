package com.example.cpayments.sipay.repository;

import com.example.cpayments.sipay.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // E-posta adresine göre müşteri bulmak için.
    Optional<Customer> findByEmail(String email);

    // Harici referans numarasına göre müşteri bulmak için.
    Optional<Customer> findByExternalRef(String externalRef);

}