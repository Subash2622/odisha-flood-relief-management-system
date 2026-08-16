package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.DonationRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.DonationResponse;
import com.odisha.floodrelief.service.DonationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Api(tags = "Donations")
@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @ApiOperation("Get preset donation amounts")
    @GetMapping("/amounts")
    public ResponseEntity<ApiResponse<List<Integer>>> getPresetAmounts() {
        return ResponseEntity.ok(ApiResponse.success(Arrays.asList(100, 500, 1000, 5000)));
    }

    @ApiOperation("Create donation (authenticated)")
    @PostMapping
    public ResponseEntity<ApiResponse<DonationResponse>> donate(@Valid @RequestBody DonationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Donation successful", donationService.createDonation(request, false)));
    }

    @ApiOperation("Guest donation")
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<DonationResponse>> guestDonate(@Valid @RequestBody DonationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Donation successful", donationService.createDonation(request, true)));
    }

    @ApiOperation("Get my donations")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getMyDonations(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(donationService.getUserDonations(pageable)));
    }

    @ApiOperation("Download donation receipt PDF")
    @GetMapping("/{donationId}/receipt")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable String donationId) {
        byte[] pdf = donationService.getReceiptPdf(donationId);
        ByteArrayResource resource = new ByteArrayResource(pdf);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"receipt_" + donationId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }

    @ApiOperation("Get donation QR image")
    @GetMapping("/{donationId}/qr")
    public ResponseEntity<Resource> downloadQr(@PathVariable String donationId) {
        byte[] image = donationService.getQrImage(donationId);
        ByteArrayResource resource = new ByteArrayResource(image);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(image.length)
                .body(resource);
    }

    @ApiOperation("Get donation by ID")
    @GetMapping("/{donationId}")
    public ResponseEntity<ApiResponse<DonationResponse>> getById(@PathVariable String donationId) {
        return ResponseEntity.ok(ApiResponse.success(donationService.getDonationById(donationId)));
    }

    @ApiOperation("Get all donations (CEO only)")
    @GetMapping("/all")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(donationService.getAllDonations(pageable)));
    }
}
