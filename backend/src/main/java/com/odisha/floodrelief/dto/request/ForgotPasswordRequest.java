package com.odisha.floodrelief.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequest {

    /** Registered email or username */
    @NotBlank(message = "Email or username is required")
    private String identifier;
}
