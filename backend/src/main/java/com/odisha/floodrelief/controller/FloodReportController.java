package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.FloodReportRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.FloodReportResponse;
import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import com.odisha.floodrelief.service.FloodReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "Flood Reports")
@RestController
@RequestMapping("/flood-reports")
@RequiredArgsConstructor
public class FloodReportController {

    private final FloodReportService floodReportService;

    @ApiOperation("Report flood (public)")
    @PostMapping("/public")
    public ResponseEntity<ApiResponse<FloodReportResponse>> reportPublic(
            @Valid @RequestPart("report") FloodReportRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ResponseEntity.ok(ApiResponse.success("Report submitted", floodReportService.createReport(request, photo, true)));
    }

    @ApiOperation("Report flood (authenticated)")
    @PostMapping
    public ResponseEntity<ApiResponse<FloodReportResponse>> report(
            @Valid @RequestPart("report") FloodReportRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ResponseEntity.ok(ApiResponse.success("Report submitted", floodReportService.createReport(request, photo, false)));
    }

    @ApiOperation("Get all flood reports")
    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<FloodReportResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(floodReportService.getAllReports(pageable)));
    }

    @ApiOperation("Get flood report by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<FloodReportResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(floodReportService.getReportById(id)));
    }

    @ApiOperation("Update flood report status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<FloodReportResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        FloodReportStatus status = FloodReportStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.success(
                floodReportService.updateStatus(id, status, body.get("remarks"))));
    }
}
