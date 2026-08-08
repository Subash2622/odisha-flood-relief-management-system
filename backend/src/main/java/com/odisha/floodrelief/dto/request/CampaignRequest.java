package com.odisha.floodrelief.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CampaignRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "1.0")
    private BigDecimal targetAmount;

    private LocalDate startDate;
    private LocalDate endDate;
}
