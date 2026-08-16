package com.odisha.floodrelief.util;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class IdGeneratorUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generateDonationId() {
        return "DON-" + LocalDateTime.now().format(FORMATTER) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String generateMembershipId() {
        return "MEM-" + LocalDateTime.now().format(FORMATTER) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String generateVolunteerId() {
        return "VOL-" + LocalDateTime.now().format(FORMATTER) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String generatePaymentId() {
        return "PAY-" + LocalDateTime.now().format(FORMATTER) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
