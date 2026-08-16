package com.odisha.floodrelief.entity;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import com.odisha.floodrelief.entity.enums.WorkStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "volunteer_id", unique = true, length = 30)
    private String volunteerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "assigned_area", length = 200)
    private String assignedArea;

    @Column(name = "assigned_district", length = 100)
    private String assignedDistrict;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_status", length = 20)
    @Builder.Default
    private WorkStatus workStatus = WorkStatus.ASSIGNED;

    @Column(name = "before_photo")
    private String beforePhoto;

    @Column(name = "after_photo")
    private String afterPhoto;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
