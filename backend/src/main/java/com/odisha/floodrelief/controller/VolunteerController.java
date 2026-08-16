package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.VolunteerResponse;
import com.odisha.floodrelief.entity.enums.WorkStatus;
import com.odisha.floodrelief.service.VolunteerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Api(tags = "Volunteers")
@RestController
@RequestMapping("/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @ApiOperation("Apply as volunteer")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<VolunteerResponse>> apply() {
        return ResponseEntity.ok(ApiResponse.success("Application submitted", volunteerService.applyVolunteer()));
    }

    @ApiOperation("Get my volunteer profile")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<VolunteerResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getMyVolunteerProfile()));
    }

    @ApiOperation("Update work status")
    @PatchMapping("/work-status")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<VolunteerResponse>> updateWorkStatus(@RequestBody Map<String, String> body) {
        WorkStatus status = WorkStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.success(
                volunteerService.updateWorkStatus(status, body.get("notes"))));
    }

    @ApiOperation("Upload before/after photos")
    @PostMapping("/photos")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<VolunteerResponse>> uploadPhotos(
            @RequestPart(value = "before", required = false) MultipartFile before,
            @RequestPart(value = "after", required = false) MultipartFile after) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.uploadPhotos(before, after)));
    }

    @ApiOperation("Get all volunteers")
    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<VolunteerResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getAllVolunteers(pageable)));
    }

    @ApiOperation("Get pending volunteer applications")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<VolunteerResponse>>> getPending(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getPendingVolunteers(pageable)));
    }

    @ApiOperation("Approve volunteer")
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Volunteer approved", volunteerService.approveVolunteer(id)));
    }

    @ApiOperation("Assign volunteer to area")
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerResponse>> assign(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(
                volunteerService.assignVolunteer(id, body.get("area"), body.get("district"))));
    }
}
