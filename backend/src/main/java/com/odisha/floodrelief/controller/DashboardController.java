package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.dto.response.AdminDashboardResponse;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.CeoDashboardResponse;
import com.odisha.floodrelief.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Dashboard")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @ApiOperation("CEO Dashboard")
    @GetMapping("/ceo")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<CeoDashboardResponse>> getCeoDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCeoDashboard()));
    }

    @ApiOperation("Admin Dashboard")
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminDashboard()));
    }
}
