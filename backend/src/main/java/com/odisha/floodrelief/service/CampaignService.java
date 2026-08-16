package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.request.CampaignRequest;
import com.odisha.floodrelief.dto.response.CampaignResponse;
import com.odisha.floodrelief.entity.Campaign;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.entity.enums.CampaignStatus;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.CampaignRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.AuditLogUtil;
import com.odisha.floodrelief.util.FileStorageUtil;
import com.odisha.floodrelief.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final QrCodeUtil qrCodeUtil;
    private final FileStorageUtil fileStorageUtil;
    private final AuditLogUtil auditLogUtil;

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request, MultipartFile banner) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(CampaignStatus.ACTIVE)
                .createdBy(user)
                .build();

        if (banner != null && !banner.isEmpty()) {
            campaign.setBannerImage(fileStorageUtil.storeFile(banner, "campaigns"));
        }

        campaign = campaignRepository.save(campaign);
        String qrPath = qrCodeUtil.generateQrCode("CAMPAIGN:" + campaign.getId(), "campaign_" + campaign.getId());
        campaign.setQrCodePath(qrPath);
        campaign = campaignRepository.save(campaign);

        auditLogUtil.log(user, "CREATE_CAMPAIGN", "Campaign", campaign.getId(), "Created campaign: " + campaign.getTitle());
        log.info("CAMPAIGN created: id={} title={}", campaign.getId(), campaign.getTitle());
        return mapToResponse(campaign);
    }

    @Transactional
    public CampaignResponse updateCampaign(Long id, CampaignRequest request, MultipartFile banner) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setTargetAmount(request.getTargetAmount());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        if (banner != null && !banner.isEmpty()) {
            campaign.setBannerImage(fileStorageUtil.storeFile(banner, "campaigns"));
        }

        return mapToResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse closeCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaign.setStatus(CampaignStatus.CLOSED);
        return mapToResponse(campaignRepository.save(campaign));
    }

    public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<CampaignResponse> getActiveCampaigns(Pageable pageable) {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE, pageable).map(this::mapToResponse);
    }

    public CampaignResponse getCampaignById(Long id) {
        return mapToResponse(campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found")));
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        double progress = 0;
        if (campaign.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = campaign.getCollectedAmount()
                    .divide(campaign.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .targetAmount(campaign.getTargetAmount())
                .collectedAmount(campaign.getCollectedAmount())
                .bannerImage(campaign.getBannerImage())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .status(campaign.getStatus())
                .qrCodePath(campaign.getQrCodePath())
                .createdByName(campaign.getCreatedBy() != null ? campaign.getCreatedBy().getFullName() : null)
                .createdAt(campaign.getCreatedAt())
                .progressPercent(progress)
                .build();
    }
}
