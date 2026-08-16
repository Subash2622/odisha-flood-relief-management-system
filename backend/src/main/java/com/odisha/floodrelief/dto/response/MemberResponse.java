package com.odisha.floodrelief.dto.response;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String membershipId;
    private String fullName;
    private String email;
    private ApprovalStatus status;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String qrCodePath;
    private LocalDateTime createdAt;
}
