package com.odisha.floodrelief.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private long pendingMembers;
    private long pendingVolunteers;
    private long activeCampaigns;
    private long pendingFloodReports;
    private long totalMembers;
    private long totalVolunteers;
    private long totalFloodReports;
}
