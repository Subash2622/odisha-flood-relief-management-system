package com.odisha.floodrelief.dto.request;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.UrgencyLevel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FloodReportRequest {

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "District is required")
    private String district;

    private Double gpsLatitude;
    private Double gpsLongitude;

    @NotBlank(message = "Description is required")
    private String description;

    private UrgencyLevel urgency;
    private String reporterName;
    private String reporterPhone;
}
