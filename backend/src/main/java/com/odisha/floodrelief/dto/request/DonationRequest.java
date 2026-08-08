package com.odisha.floodrelief.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DonationRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Minimum donation is 1")
    private BigDecimal amount;

    private Long campaignId;
    private String donorName;
    private String donorEmail;
    private String donorPhone;
    private Boolean isAnonymous;
    private String message;
    private String paymentMethod;
}
