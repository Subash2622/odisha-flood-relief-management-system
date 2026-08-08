package com.odisha.floodrelief.dto.response;

import com.odisha.floodrelief.entity.enums.ReliefItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReliefDistributionResponse {
    private Long id;
    private ReliefItemType itemType;
    private Integer quantity;
    private String village;
    private String district;
    private String campPhoto;
    private String notes;
    private Boolean distributionCompleted;
    private String volunteerName;
    private String distributedByName;
    private LocalDateTime createdAt;
}
