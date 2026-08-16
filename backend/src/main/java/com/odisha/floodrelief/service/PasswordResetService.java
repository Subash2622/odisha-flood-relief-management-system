package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.ForgotPasswordRequest;
import com.odisha.floodrelief.dto.request.ResetPasswordRequest;
import com.odisha.floodrelief.dto.response.ForgotPasswordResponse;
import com.odisha.floodrelief.dto.response.ResetPasswordResponse;
import com.odisha.floodrelief.entity.PasswordResetOtp;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.entity.enums.OtpChannel;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.PasswordResetOtpRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.util.AuditLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationDispatchService notificationDispatchService;
    private final AuditLogUtil auditLogUtil;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    @Value("${app.otp.dev-mode:false}")
    private boolean otpDevMode;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ForgotPasswordResponse requestOtp(ForgotPasswordRequest request) {
        String identifier = request.getIdentifier() == null ? "" : request.getIdentifier().trim();
        if (identifier.isEmpty()) {
            throw new BadRequestException("Email or username is required");
        }

        User user = findRegisteredUser(identifier);

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BadRequestException("This account is disabled. Contact admin/CEO.");
        }

        String destination = user.getEmail();
        if (destination == null || destination.trim().isEmpty()) {
            throw new BadRequestException("No email registered for this account");
        }
        destination = destination.trim();

        otpRepository.invalidateAllForUser(user.getId());

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String resetToken = UUID.randomUUID().toString().replace("-", "");

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .resetToken(resetToken)
                .user(user)
                .otpHash(passwordEncoder.encode(otp))
                .channel(OtpChannel.EMAIL)
                .destination(destination)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .attemptCount(0)
                .build();
        otpRepository.saveAndFlush(resetOtp);

        boolean delivered = notificationDispatchService.sendEmailOtp(destination, otp);

        ForgotPasswordResponse.ForgotPasswordResponseBuilder builder = ForgotPasswordResponse.builder()
                .message(delivered
                        ? "OTP sent to your registered email. Check inbox (and Spam)."
                        : "OTP generated (dev fallback).")
                .channel(OtpChannel.EMAIL.name())
                .maskedDestination(maskEmail(destination))
                .resetToken(resetToken)
                .usernameHint(user.getUsername());

        // Only when explicitly in dev mode AND email was not delivered
        if (otpDevMode && !delivered) {
            builder.devOtp(otp);
            log.warn("OTP DEV MODE: user={}, username={}, otp={}", user.getId(), user.getUsername(), otp);
        } else {
            log.info("OTP emailed for username={} (not shown in API)", user.getUsername());
        }

        return builder.build();
    }

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        String token = request.getResetToken().trim();
        String otp = request.getOtp().trim();
        String newPassword = request.getNewPassword();

        PasswordResetOtp resetOtp = otpRepository.findByResetTokenAndUsedFalse(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset session. Please request OTP again."));

        if (Boolean.TRUE.equals(resetOtp.getUsed())) {
            throw new BadRequestException("This OTP was already used. Please request a new one.");
        }

        if (resetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            resetOtp.setUsed(true);
            otpRepository.save(resetOtp);
            throw new BadRequestException("OTP expired. Please request a new one.");
        }

        int attempts = resetOtp.getAttemptCount() == null ? 0 : resetOtp.getAttemptCount();
        if (attempts >= 5) {
            resetOtp.setUsed(true);
            otpRepository.save(resetOtp);
            throw new BadRequestException("Too many invalid attempts. Please request a new OTP.");
        }

        if (!passwordEncoder.matches(otp, resetOtp.getOtpHash())) {
            resetOtp.setAttemptCount(attempts + 1);
            otpRepository.save(resetOtp);
            throw new BadRequestException("Invalid OTP. Please check and try again.");
        }

        User user = resetOtp.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User linked to this OTP was not found");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        int updated = userRepository.updatePassword(user.getId(), encodedPassword);
        if (updated != 1) {
            throw new BadRequestException("Failed to update password in database. User may not exist.");
        }

        resetOtp.setUsed(true);
        otpRepository.saveAndFlush(resetOtp);

        User refreshed = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after password update"));
        if (!passwordEncoder.matches(newPassword, refreshed.getPassword())) {
            log.error("Password update verification failed for userId={}", user.getId());
            throw new BadRequestException("Password update failed verification. Please try again.");
        }

        auditLogUtil.log(refreshed, "PASSWORD_RESET", "User", refreshed.getId(), "Password reset via email OTP successful");
        log.info("Password reset successful for username={}", refreshed.getUsername());

        return ResetPasswordResponse.builder()
                .message("Password updated successfully. Login with username: " + refreshed.getUsername())
                .username(refreshed.getUsername())
                .email(refreshed.getEmail())
                .build();
    }

    private User findRegisteredUser(String identifier) {
        return userRepository.findByEmailIgnoreCase(identifier)
                .orElseGet(() -> userRepository.findByUsernameIgnoreCase(identifier)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No user found with this email/username in database")));
    }

    private String maskEmail(String destination) {
        int at = destination.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return destination.charAt(0) + "***" + destination.substring(at);
    }
}
