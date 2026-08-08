package com.odisha.floodrelief.dto.request;

import com.odisha.floodrelief.entity.enums.ReliefItemType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ReliefDistributionRequest {

    @NotNull(message = "Item type is required")
    private ReliefItemType itemType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1)
    private Integer quantity;

    private String village;
    private String district;
    private Long volunteerId;
    private String notes;
}
