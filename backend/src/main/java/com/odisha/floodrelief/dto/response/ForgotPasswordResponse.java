package com.odisha.floodrelief.dto.response;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    private String channel;
    private String maskedDestination;
    /** Required in reset-password step to update the correct user. */
    private String resetToken;
    private String usernameHint;
    /** Present only when app.otp.dev-mode=true (for local testing). */
    private String devOtp;
}
