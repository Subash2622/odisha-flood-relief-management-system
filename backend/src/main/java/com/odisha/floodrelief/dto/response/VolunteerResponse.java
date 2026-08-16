package com.odisha.floodrelief.dto.response;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import com.odisha.floodrelief.entity.enums.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerResponse {

    private Long id;
    private String volunteerId;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private ApprovalStatus status;
    private String assignedArea;
    private String assignedDistrict;
    private WorkStatus workStatus;
    private String beforePhoto;
    private String afterPhoto;
    private String notes;
    private LocalDateTime createdAt;
}
