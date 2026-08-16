package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.AdminDashboardResponse;
import com.odisha.floodrelief.dto.response.CeoDashboardResponse;
import com.odisha.floodrelief.dto.response.DonationResponse;
import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import com.odisha.floodrelief.entity.enums.CampaignStatus;
import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import com.odisha.floodrelief.entity.enums.PaymentStatus;
import com.odisha.floodrelief.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DonationRepository donationRepository;
    private final MemberRepository memberRepository;
    private final VolunteerRepository volunteerRepository;
    private final CampaignRepository campaignRepository;
    private final FloodReportRepository floodReportRepository;
    private final DonationService donationService;

    public CeoDashboardResponse getCeoDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        LocalDateTime yearStart = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime yearEnd = yearStart.plusYears(1);

        BigDecimal total = donationRepository.sumTotalDonations();
        BigDecimal today = donationRepository.sumDonationsBetween(todayStart, todayEnd);
        BigDecimal monthly = donationRepository.sumDonationsBetween(monthStart, monthEnd);
        BigDecimal yearly = donationRepository.sumDonationsBetween(yearStart, yearEnd);

        List<DonationResponse> recent = donationRepository.findAll(PageRequest.of(0, 10))
                .map(donationService::mapToResponse)
                .getContent();

        List<Map<String, Object>> topDonors = donationRepository.findTopDonors(PageRequest.of(0, 5))
                .stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", row[0]);
                    map.put("amount", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Long> districtReports = new HashMap<>();
        floodReportRepository.countByDistrict().forEach(row ->
                districtReports.put((String) row[0], (Long) row[1]));

        Map<String, BigDecimal> monthlyTrend = new HashMap<>();
        donationRepository.monthlyDonationsByYear(LocalDate.now().getYear()).forEach(row ->
                monthlyTrend.put("Month-" + row[0], (BigDecimal) row[1]));

        return CeoDashboardResponse.builder()
                .totalDonations(total != null ? total : BigDecimal.ZERO)
                .todayDonations(today != null ? today : BigDecimal.ZERO)
                .monthlyDonations(monthly != null ? monthly : BigDecimal.ZERO)
                .yearlyDonations(yearly != null ? yearly : BigDecimal.ZERO)
                .totalMembers(memberRepository.countByStatus(ApprovalStatus.APPROVED))
                .totalVolunteers(volunteerRepository.countByStatus(ApprovalStatus.APPROVED))
                .activeCampaigns(campaignRepository.countByStatus(CampaignStatus.ACTIVE))
                .floodReports(floodReportRepository.count())
                .amountDistributed(BigDecimal.ZERO)
                .pendingReliefRequests(floodReportRepository.countByStatus(FloodReportStatus.PENDING))
                .recentTransactions(recent)
                .topDonors(topDonors)
                .monthlyTrend(monthlyTrend)
                .districtReports(districtReports)
                .build();
    }

    public AdminDashboardResponse getAdminDashboard() {
        return AdminDashboardResponse.builder()
                .pendingMembers(memberRepository.countByStatus(ApprovalStatus.PENDING))
                .pendingVolunteers(volunteerRepository.countByStatus(ApprovalStatus.PENDING))
                .activeCampaigns(campaignRepository.countByStatus(CampaignStatus.ACTIVE))
                .pendingFloodReports(floodReportRepository.countByStatus(FloodReportStatus.PENDING))
                .totalMembers(memberRepository.countByStatus(ApprovalStatus.APPROVED))
                .totalVolunteers(volunteerRepository.countByStatus(ApprovalStatus.APPROVED))
                .totalFloodReports(floodReportRepository.count())
                .build();
    }
}
