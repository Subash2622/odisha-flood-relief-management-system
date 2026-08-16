package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.HomePopupRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.HomePopupResponse;
import com.odisha.floodrelief.service.HomePopupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Home Popups")
@RestController
@RequiredArgsConstructor
public class HomePopupController {

    private final HomePopupService homePopupService;

    @ApiOperation("Get active home popups (public)")
    @GetMapping("/public/popups")
    public ResponseEntity<ApiResponse<List<HomePopupResponse>>> getActivePopups() {
        return ResponseEntity.ok(ApiResponse.success(homePopupService.getActivePopups()));
    }

    @ApiOperation("List all popups (CEO)")
    @GetMapping("/ceo/popups")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<List<HomePopupResponse>>> getAllPopups() {
        return ResponseEntity.ok(ApiResponse.success(homePopupService.getAllPopups()));
    }

    @ApiOperation("Create home popup (CEO)")
    @PostMapping("/ceo/popups")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<HomePopupResponse>> create(@Valid @RequestBody HomePopupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Popup created", homePopupService.createPopup(request)));
    }

    @ApiOperation("Toggle popup active status (CEO)")
    @PatchMapping("/ceo/popups/{id}/toggle")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<HomePopupResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(homePopupService.toggleActive(id)));
    }

    @ApiOperation("Delete popup (CEO)")
    @DeleteMapping("/ceo/popups/{id}")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        homePopupService.deletePopup(id);
        return ResponseEntity.ok(ApiResponse.success("Popup deleted", null));
    }
}
