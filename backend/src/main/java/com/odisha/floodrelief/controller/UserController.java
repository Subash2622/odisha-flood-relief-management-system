package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.UserResponse;
import com.odisha.floodrelief.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "Users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("Update profile")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(fullName, phone, address, profileImage)));
    }

    @ApiOperation("Get user by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @ApiOperation("Search users")
    @GetMapping("/search")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.searchUsers(keyword, pageable)));
    }
}
