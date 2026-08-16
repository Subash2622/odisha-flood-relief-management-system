package com.odisha.floodrelief.dto.response;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponse {

    private Long id;
    private String donationId;
    private BigDecimal amount;
    private String donorName;
    private String donorEmail;
    private PaymentStatus status;
    private String campaignTitle;
    private Long campaignId;
    private String qrCodePath;
    private String receiptPath;
    private String message;
    private LocalDateTime createdAt;
}
