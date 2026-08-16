package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(Long userId);

    Optional<PasswordResetOtp> findByResetTokenAndUsedFalse(String resetToken);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PasswordResetOtp o SET o.used = true WHERE o.user.id = :userId AND o.used = false")
    int invalidateAllForUser(@Param("userId") Long userId);
}
