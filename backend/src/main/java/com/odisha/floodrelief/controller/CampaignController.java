package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.CampaignRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.CampaignResponse;
import com.odisha.floodrelief.service.CampaignService;
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

@Api(tags = "Campaigns")
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @ApiOperation("Get all campaigns")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.getAllCampaigns(pageable)));
    }

    @ApiOperation("Get active campaigns")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getActive(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.getActiveCampaigns(pageable)));
    }

    @ApiOperation("Get campaign by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.getCampaignById(id)));
    }

    @ApiOperation("Create campaign")
    @PostMapping
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<CampaignResponse>> create(
            @Valid @RequestPart("campaign") CampaignRequest request,
            @RequestPart(value = "banner", required = false) MultipartFile banner) {
        return ResponseEntity.ok(ApiResponse.success("Campaign created", campaignService.createCampaign(request, banner)));
    }

    @ApiOperation("Update campaign")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<CampaignResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("campaign") CampaignRequest request,
            @RequestPart(value = "banner", required = false) MultipartFile banner) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.updateCampaign(id, request, banner)));
    }

    @ApiOperation("Close campaign")
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<CampaignResponse>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Campaign closed", campaignService.closeCampaign(id)));
    }
}
