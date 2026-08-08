package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Page<PaymentTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
