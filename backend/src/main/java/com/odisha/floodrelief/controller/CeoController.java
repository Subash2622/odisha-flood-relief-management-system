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

import java.util.Map;

@Api(tags = "CEO")
@RestController
@RequestMapping("/ceo")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CEO')")
public class CeoController {

    private final UserService userService;

    @ApiOperation("Get all users")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
    }

    @ApiOperation("Create admin")
    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success("Admin created",
                userService.createAdmin(body.get("username"), body.get("email"),
                        body.get("password"), body.get("fullName"))));
    }

    @ApiOperation("Disable user")
    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<ApiResponse<String>> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(ApiResponse.success("User disabled", null));
    }

    @ApiOperation("Delete user")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
