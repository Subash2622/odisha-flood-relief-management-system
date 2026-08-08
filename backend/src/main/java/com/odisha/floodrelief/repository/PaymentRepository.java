package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    @EntityGraph(attributePaths = {"donation"})
    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
