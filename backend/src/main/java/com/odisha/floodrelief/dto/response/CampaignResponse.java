package com.odisha.floodrelief.dto.response;

import com.odisha.floodrelief.entity.enums.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal collectedAmount;
    private String bannerImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private CampaignStatus status;
    private String qrCodePath;
    private String createdByName;
    private LocalDateTime createdAt;
    private double progressPercent;
}
