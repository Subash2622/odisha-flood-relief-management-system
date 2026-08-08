package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.Donation;
import com.odisha.floodrelief.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Optional<Donation> findByDonationId(String donationId);

    Page<Donation> findByUserId(Long userId, Pageable pageable);

    List<Donation> findByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'SUCCESS'")
    BigDecimal sumTotalDonations();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'SUCCESS' AND d.createdAt >= :start AND d.createdAt < :end")
    BigDecimal sumDonationsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT d.donorName, SUM(d.amount) as total FROM Donation d WHERE d.status = 'SUCCESS' AND d.isAnonymous = false GROUP BY d.donorName ORDER BY total DESC")
    List<Object[]> findTopDonors(Pageable pageable);

    @Query("SELECT FUNCTION('MONTH', d.createdAt), SUM(d.amount) FROM Donation d WHERE d.status = 'SUCCESS' AND YEAR(d.createdAt) = :year GROUP BY FUNCTION('MONTH', d.createdAt)")
    List<Object[]> monthlyDonationsByYear(@Param("year") int year);
}
