package com.odisha.floodrelief.entity;

import com.odisha.floodrelief.entity.enums.ReliefItemType;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, unique = true, length = 30)
    private ReliefItemType itemType;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "min_threshold")
    @Builder.Default
    private Integer minThreshold = 10;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
