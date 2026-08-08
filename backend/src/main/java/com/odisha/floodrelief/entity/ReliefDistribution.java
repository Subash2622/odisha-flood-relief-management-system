package com.odisha.floodrelief.entity;

import com.odisha.floodrelief.entity.enums.ReliefItemType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relief_distribution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReliefDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributed_by")
    private User distributedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 30)
    private ReliefItemType itemType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 100)
    private String village;

    @Column(length = 100)
    private String district;

    @Column(name = "camp_photo")
    private String campPhoto;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "distribution_completed")
    @Builder.Default
    private Boolean distributionCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
