package com.odisha.floodrelief.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String resetToken;

    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 8)
    private String otp;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100)
    private String newPassword;
}
