package com.odisha.floodrelief.entity;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import com.odisha.floodrelief.entity.enums.UrgencyLevel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flood_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloodReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reporter_name", length = 100)
    private String reporterName;

    @Column(name = "reporter_phone", length = 15)
    private String reporterPhone;

    @Column(length = 100)
    private String village;

    @Column(length = 100)
    private String district;

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private UrgencyLevel urgency = UrgencyLevel.MEDIUM;

    @Column(name = "photo_path")
    private String photoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FloodReportStatus status = FloodReportStatus.PENDING;

    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by")
    private User handledBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
