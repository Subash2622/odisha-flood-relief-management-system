package com.odisha.floodrelief.dto.response;

import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import com.odisha.floodrelief.entity.enums.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloodReportResponse {

    private Long id;
    private String reporterName;
    private String reporterPhone;
    private String village;
    private String district;
    private Double gpsLatitude;
    private Double gpsLongitude;
    private String description;
    private UrgencyLevel urgency;
    private String photoPath;
    private FloodReportStatus status;
    private String adminRemarks;
    private LocalDateTime createdAt;
}
