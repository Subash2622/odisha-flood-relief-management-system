package com.odisha.floodrelief.entity;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_name", length = 200)
    private String orgName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc", length = 20)
    private String bankIfsc;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "qr_payment_path")
    private String qrPaymentPath;

    @Column(name = "home_banner")
    private String homeBanner;

    @Column(name = "logo_path")
    private String logoPath;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
