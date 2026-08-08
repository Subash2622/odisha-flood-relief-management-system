package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.Campaign;
import com.odisha.floodrelief.entity.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findByStatus(CampaignStatus status, Pageable pageable);

    List<Campaign> findByStatus(CampaignStatus status);

    long countByStatus(CampaignStatus status);
}
