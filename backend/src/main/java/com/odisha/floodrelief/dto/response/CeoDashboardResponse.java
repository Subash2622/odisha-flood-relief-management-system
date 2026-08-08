package com.odisha.floodrelief.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CeoDashboardResponse {

    private BigDecimal totalDonations;
    private BigDecimal todayDonations;
    private BigDecimal monthlyDonations;
    private BigDecimal yearlyDonations;
    private long totalMembers;
    private long totalVolunteers;
    private long activeCampaigns;
    private long floodReports;
    private BigDecimal amountDistributed;
    private long pendingReliefRequests;
    private List<DonationResponse> recentTransactions;
    private List<Map<String, Object>> topDonors;
    private Map<String, BigDecimal> monthlyTrend;
    private Map<String, Long> districtReports;
}
