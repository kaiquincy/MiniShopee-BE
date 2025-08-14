package com.example.demo.repository;
import com.example.demo.model.Payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Method to find a payment by its ID
    Optional<Payment> getPaymentById(Long id);

    // Method to find a payment by its transaction ID
    Optional<Payment> getPaymentByTransactionId(String transactionId);
}
