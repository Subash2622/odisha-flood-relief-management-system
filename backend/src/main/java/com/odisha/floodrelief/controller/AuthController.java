package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.dto.request.ForgotPasswordRequest;
import com.odisha.floodrelief.dto.request.LoginRequest;
import com.odisha.floodrelief.dto.request.RegisterRequest;
import com.odisha.floodrelief.dto.request.ResetPasswordRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.ForgotPasswordResponse;
import com.odisha.floodrelief.dto.response.JwtResponse;
import com.odisha.floodrelief.dto.response.ResetPasswordResponse;
import com.odisha.floodrelief.dto.response.UserResponse;
import com.odisha.floodrelief.service.AuthService;
import com.odisha.floodrelief.service.PasswordResetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @ApiOperation("Register new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authService.register(request)));
    }

    @ApiOperation("Login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @ApiOperation("Request password reset OTP (email only)")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success("OTP sent", passwordResetService.requestOtp(request)));
    }

    @ApiOperation("Reset password using OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Password reset successful", passwordResetService.resetPassword(request)));
    }

    @ApiOperation("Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(body.get("refreshToken"))));
    }

    @ApiOperation("Get current user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser()));
    }

    @ApiOperation("Logout")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
