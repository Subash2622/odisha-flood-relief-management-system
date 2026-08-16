package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.FloodReportRequest;
import com.odisha.floodrelief.dto.response.FloodReportResponse;
import com.odisha.floodrelief.entity.FloodReport;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.FloodReportRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloodReportService {

    private final FloodReportRepository floodReportRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;

    @Transactional
    public FloodReportResponse createReport(FloodReportRequest request, MultipartFile photo, boolean isPublic) {
        User user = null;
        if (!isPublic) {
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepository.findById(principal.getId()).orElse(null);
        }

        FloodReport report = FloodReport.builder()
                .user(user)
                .reporterName(request.getReporterName())
                .reporterPhone(request.getReporterPhone())
                .village(request.getVillage())
                .district(request.getDistrict())
                .gpsLatitude(request.getGpsLatitude())
                .gpsLongitude(request.getGpsLongitude())
                .description(request.getDescription())
                .urgency(request.getUrgency())
                .status(FloodReportStatus.PENDING)
                .build();

        if (photo != null && !photo.isEmpty()) {
            report.setPhotoPath(fileStorageUtil.storeFile(photo, "flood-reports"));
        }

        FloodReport saved = floodReportRepository.save(report);
        log.info("FLOOD_REPORT created: id={} district={} village={} public={}",
                saved.getId(), saved.getDistrict(), saved.getVillage(), isPublic);
        return mapToResponse(saved);
    }

    @Transactional
    public FloodReportResponse updateStatus(Long id, FloodReportStatus status, String remarks) {
        FloodReport report = floodReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flood report not found"));

        report.setStatus(status);
        report.setAdminRemarks(remarks);

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.findById(principal.getId()).ifPresent(report::setHandledBy);

        FloodReport saved = floodReportRepository.save(report);
        log.info("FLOOD_REPORT status updated: id={} status={}", saved.getId(), status);
        return mapToResponse(saved);
    }

    public Page<FloodReportResponse> getAllReports(Pageable pageable) {
        return floodReportRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<FloodReportResponse> getReportsByStatus(FloodReportStatus status, Pageable pageable) {
        return floodReportRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    public FloodReportResponse getReportById(Long id) {
        return mapToResponse(floodReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flood report not found")));
    }

    private FloodReportResponse mapToResponse(FloodReport report) {
        return FloodReportResponse.builder()
                .id(report.getId())
                .reporterName(report.getReporterName())
                .reporterPhone(report.getReporterPhone())
                .village(report.getVillage())
                .district(report.getDistrict())
                .gpsLatitude(report.getGpsLatitude())
                .gpsLongitude(report.getGpsLongitude())
                .description(report.getDescription())
                .urgency(report.getUrgency())
                .photoPath(report.getPhotoPath())
                .status(report.getStatus())
                .adminRemarks(report.getAdminRemarks())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
