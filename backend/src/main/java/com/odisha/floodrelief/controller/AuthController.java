package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

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

import com.odisha.floodrelief.service.AuthService;
import com.odisha.floodrelief.service.PasswordResetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
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
        log.info("AUTH register requested for username={}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authService.register(request)));
    }

    @ApiOperation("Login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("AUTH login attempted for identifier={}", request.getUsernameOrEmail());
        JwtResponse jwt = authService.login(request);
        log.info("AUTH login success for username={}", jwt.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwt));
    }

    @ApiOperation("Request password reset OTP (email only)")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("AUTH forgot-password requested for identifier={}", request.getIdentifier());
        return ResponseEntity.ok(ApiResponse.success("OTP sent", passwordResetService.requestOtp(request)));
    }

    @ApiOperation("Reset password using OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("AUTH reset-password attempted");
        ResetPasswordResponse result = passwordResetService.resetPassword(request);
        log.info("AUTH reset-password success for username={}", result.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", result));
    }

    @ApiOperation("Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@RequestBody Map<String, String> body) {
        log.info("AUTH token refresh requested");
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
        log.info("AUTH logout");
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
