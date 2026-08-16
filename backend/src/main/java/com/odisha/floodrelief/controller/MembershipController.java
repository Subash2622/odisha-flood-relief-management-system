package com.odisha.floodrelief.controller;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.MemberResponse;
import com.odisha.floodrelief.service.MembershipService;
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

@Api(tags = "Membership")
@RestController
@RequestMapping("/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @ApiOperation("Apply for membership")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<MemberResponse>> apply() {
        return ResponseEntity.ok(ApiResponse.success("Application submitted", membershipService.applyMembership()));
    }

    @ApiOperation("Get my membership")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN', 'MEMBER')")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyMembership() {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getMyMembership()));
    }

    @ApiOperation("Download my membership card PDF")
    @GetMapping("/my/card")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN', 'MEMBER')")
    public ResponseEntity<Resource> downloadCard() {
        byte[] pdf = membershipService.getMyMembershipCardPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=membership-card.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }

    @ApiOperation("Renew membership")
    @PostMapping("/renew")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<MemberResponse>> renew() {
        return ResponseEntity.ok(ApiResponse.success("Renewal request submitted", membershipService.renewMembership()));
    }

    @ApiOperation("Get all members")
    @GetMapping
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getAllMembers(pageable)));
    }

    @ApiOperation("Get pending membership applications")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getPending(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getPendingMembers(pageable)));
    }

    @ApiOperation("Approve membership")
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Membership approved", membershipService.approveMembership(id)));
    }
}
