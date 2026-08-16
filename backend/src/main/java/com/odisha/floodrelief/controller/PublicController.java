package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.entity.Announcement;
import com.odisha.floodrelief.entity.OrganizationDetails;
import com.odisha.floodrelief.service.OrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(tags = "Public & Organization")
@RestController
@RequiredArgsConstructor
public class PublicController {

    private final OrganizationService organizationService;

    @ApiOperation("Get organization details (public)")
    @GetMapping("/public/organization")
    public ResponseEntity<ApiResponse<OrganizationDetails>> getOrganization() {
        OrganizationDetails details = organizationService.getOrganizationDetails();
        details.setBankAccountNumber(null);
        details.setBankIfsc(null);
        details.setBankName(null);
        return ResponseEntity.ok(ApiResponse.success(details));
    }

    @ApiOperation("Get active announcements")
    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAnnouncements() {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getActiveAnnouncements()));
    }

    @ApiOperation("Contact NGO")
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<String>> contact(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", null));
    }

    @ApiOperation("Update organization (CEO only)")
    @PutMapping("/ceo/organization")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<OrganizationDetails>> updateOrganization(
            @RequestPart("details") OrganizationDetails details,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "qrPayment", required = false) MultipartFile qrPayment) {
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.updateOrganizationDetails(details, banner, logo, qrPayment)));
    }

    @ApiOperation("Create announcement (CEO only)")
    @PostMapping("/ceo/announcements")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.createAnnouncement(body.get("title"), body.get("content"))));
    }

    @ApiOperation("Get full organization details with bank info (CEO only)")
    @GetMapping("/ceo/organization")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<OrganizationDetails>> getFullOrganization() {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getOrganizationDetails()));
    }
}
