package com.odisha.floodrelief.dto.request;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequest {

    /** Registered email or username */
    @NotBlank(message = "Email or username is required")
    private String identifier;
}
