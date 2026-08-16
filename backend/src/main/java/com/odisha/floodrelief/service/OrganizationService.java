package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.Announcement;
import com.odisha.floodrelief.entity.OrganizationDetails;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.AnnouncementRepository;
import com.odisha.floodrelief.repository.OrganizationDetailsRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.FileStorageUtil;
import com.odisha.floodrelief.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationDetailsRepository organizationDetailsRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final QrCodeUtil qrCodeUtil;

    public OrganizationDetails getOrganizationDetails() {
        return organizationDetailsRepository.findAll().stream().findFirst()
                .orElse(OrganizationDetails.builder().orgName("Odisha Flood Relief Foundation").build());
    }

    @Transactional
    public OrganizationDetails updateOrganizationDetails(OrganizationDetails details, MultipartFile banner, MultipartFile logo, MultipartFile qrPayment) {
        OrganizationDetails existing = organizationDetailsRepository.findAll().stream().findFirst().orElse(details);

        if (details.getOrgName() != null) existing.setOrgName(details.getOrgName());
        if (details.getDescription() != null) existing.setDescription(details.getDescription());
        if (details.getEmail() != null) existing.setEmail(details.getEmail());
        if (details.getPhone() != null) existing.setPhone(details.getPhone());
        if (details.getAddress() != null) existing.setAddress(details.getAddress());
        if (details.getBankName() != null) existing.setBankName(details.getBankName());
        if (details.getBankAccountNumber() != null) existing.setBankAccountNumber(details.getBankAccountNumber());
        if (details.getBankIfsc() != null) existing.setBankIfsc(details.getBankIfsc());
        if (details.getUpiId() != null) existing.setUpiId(details.getUpiId());

        if (banner != null && !banner.isEmpty()) {
            existing.setHomeBanner(fileStorageUtil.storeFile(banner, "banners"));
        }
        if (logo != null && !logo.isEmpty()) {
            existing.setLogoPath(fileStorageUtil.storeFile(logo, "logos"));
        }
        if (qrPayment != null && !qrPayment.isEmpty()) {
            existing.setQrPaymentPath(fileStorageUtil.storeFile(qrPayment, "payment-qr"));
        } else if (details.getUpiId() != null) {
            existing.setQrPaymentPath(qrCodeUtil.generateQrCode("upi://pay?pa=" + details.getUpiId(), "upi_payment"));
        }

        return organizationDetailsRepository.save(existing);
    }

    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public Announcement createAnnouncement(String title, String content) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Announcement announcement = Announcement.builder()
                .title(title)
                .content(content)
                .isActive(true)
                .createdBy(user)
                .build();

        return announcementRepository.save(announcement);
    }
}
