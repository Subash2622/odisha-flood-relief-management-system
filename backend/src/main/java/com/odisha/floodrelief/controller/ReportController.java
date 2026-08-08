package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.dto.request.ReliefDistributionRequest;
import com.odisha.floodrelief.dto.response.ApiResponse;
import com.odisha.floodrelief.dto.response.AuditLogResponse;
import com.odisha.floodrelief.dto.response.PaymentResponse;
import com.odisha.floodrelief.dto.response.ReliefDistributionResponse;
import com.odisha.floodrelief.service.ReliefService;
import com.odisha.floodrelief.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Api(tags = "Reports & Relief")
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReliefService reliefService;

    @ApiOperation("Export donations Excel (CEO only)")
    @GetMapping("/ceo/reports/donations/excel")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportDonationsExcel() throws Exception {
        return fileResponse(reportService.exportDonationsExcel(), "donations.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @ApiOperation("Export donations PDF (CEO only)")
    @GetMapping("/ceo/reports/donations/pdf")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportDonationsPdf() throws Exception {
        return fileResponse(reportService.exportDonationsPdf(), "donations.pdf", MediaType.APPLICATION_PDF);
    }

    @ApiOperation("Export members Excel (CEO only)")
    @GetMapping("/ceo/reports/members/excel")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportMembersExcel() throws Exception {
        return fileResponse(reportService.exportMembersExcel(), "members.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @ApiOperation("Export volunteers Excel (CEO only)")
    @GetMapping("/ceo/reports/volunteers/excel")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportVolunteersExcel() throws Exception {
        return fileResponse(reportService.exportVolunteersExcel(), "volunteers.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @ApiOperation("Export campaigns Excel (CEO only)")
    @GetMapping("/ceo/reports/campaigns/excel")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportCampaignsExcel() throws Exception {
        return fileResponse(reportService.exportCampaignsExcel(), "campaigns.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @ApiOperation("Export flood reports Excel (CEO only)")
    @GetMapping("/ceo/reports/flood-reports/excel")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<byte[]> exportFloodReportsExcel() throws Exception {
        return fileResponse(reportService.exportFloodReportsExcel(), "flood-reports.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @ApiOperation("Get all payments (CEO only)")
    @GetMapping("/ceo/payments")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAllPayments(pageable)));
    }

    @ApiOperation("Get audit logs (CEO only)")
    @GetMapping("/ceo/audit-logs")
    @PreAuthorize("hasRole('CEO')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAuditLogs(pageable)));
    }

    @ApiOperation("Record relief distribution")
    @PostMapping("/admin/relief/distribute")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReliefDistributionResponse>> distribute(
            @Valid @RequestPart("distribution") ReliefDistributionRequest request,
            @RequestPart(value = "campPhoto", required = false) MultipartFile campPhoto) {
        return ResponseEntity.ok(ApiResponse.success("Distribution recorded",
                reliefService.distribute(request, campPhoto)));
    }

    @ApiOperation("Get relief distribution history")
    @GetMapping("/admin/relief/history")
    @PreAuthorize("hasAnyRole('CEO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReliefDistributionResponse>>> getHistory(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reliefService.getDistributionHistory(pageable)));
    }

    @ApiOperation("Get my relief distributions (Volunteer)")
    @GetMapping("/volunteer/relief/my")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ApiResponse<Page<ReliefDistributionResponse>>> getMyDistributions(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reliefService.getMyDistributions(pageable)));
    }

    private ResponseEntity<byte[]> fileResponse(byte[] data, String filename, MediaType mediaType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(data);
    }
}
