package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.NotificationResponse;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Notifications")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ReportService reportService;

    @ApiOperation("Get my notifications")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getMyNotifications(pageable, principal.getId())));
    }
}
