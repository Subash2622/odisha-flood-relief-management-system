package com.odisha.floodrelief.service;

import com.odisha.floodrelief.dto.request.DonationRequest;
import com.odisha.floodrelief.dto.response.DonationResponse;
import com.odisha.floodrelief.entity.*;
import com.odisha.floodrelief.entity.enums.PaymentStatus;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.*;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final IdGeneratorUtil idGeneratorUtil;
    private final QrCodeUtil qrCodeUtil;
    private final PdfUtil pdfUtil;
    private final AuditLogUtil auditLogUtil;

    @Transactional
    public DonationResponse createDonation(DonationRequest request, boolean isGuest) {
        User user = null;
        if (!isGuest) {
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepository.findById(principal.getId()).orElse(null);
        }

        Campaign campaign = null;
        if (request.getCampaignId() != null) {
            campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        }

        String donationId = idGeneratorUtil.generateDonationId();
        String donorName = request.getIsAnonymous() != null && request.getIsAnonymous()
                ? "Anonymous" : (request.getDonorName() != null ? request.getDonorName()
                : (user != null ? user.getFullName() : "Guest Donor"));

        Donation donation = Donation.builder()
                .donationId(donationId)
                .user(user)
                .campaign(campaign)
                .amount(request.getAmount())
                .donorName(donorName)
                .donorEmail(request.getDonorEmail() != null ? request.getDonorEmail() : (user != null ? user.getEmail() : null))
                .donorPhone(request.getDonorPhone())
                .isAnonymous(request.getIsAnonymous() != null && request.getIsAnonymous())
                .message(request.getMessage())
                .status(PaymentStatus.SUCCESS)
                .build();

        String qrPath = qrCodeUtil.generateQrCode("DONATION:" + donationId, donationId);
        donation.setQrCodePath(qrPath);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String receiptPath = pdfUtil.generateDonationReceipt(donationId, donorName, request.getAmount().toString(), dateStr);
        donation.setReceiptPath(receiptPath);

        donation = donationRepository.save(donation);

        Payment payment = Payment.builder()
                .paymentId(idGeneratorUtil.generatePaymentId())
                .donation(donation)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI")
                .status(PaymentStatus.SUCCESS)
                .build();
        payment = paymentRepository.save(payment);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionId(idGeneratorUtil.generateTransactionId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .remarks("Donation processed successfully")
                .build();
        transactionRepository.save(transaction);

        if (campaign != null) {
            campaign.setCollectedAmount(campaign.getCollectedAmount().add(request.getAmount()));
            campaignRepository.save(campaign);
        }

        if (user != null) {
            auditLogUtil.log(user, "DONATE", "Donation", donation.getId(), "Donated Rs. " + request.getAmount());
        }

        return mapToResponse(donation);
    }

    public Page<DonationResponse> getUserDonations(Pageable pageable) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return donationRepository.findByUserId(principal.getId(), pageable).map(this::mapToResponse);
    }

    public DonationResponse getDonationById(String donationId) {
        return mapToResponse(donationRepository.findByDonationId(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found")));
    }

    public Page<DonationResponse> getAllDonations(Pageable pageable) {
        return donationRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional
    public byte[] getReceiptPdf(String donationId) {
        Donation donation = donationRepository.findByDonationId(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        try {
            byte[] existing = pdfUtil.readFileBytes(donation.getReceiptPath());
            if (existing != null && existing.length > 0) {
                return existing;
            }
            String dateStr = donation.getCreatedAt() != null
                    ? donation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            String path = pdfUtil.generateDonationReceipt(
                    donation.getDonationId(),
                    donation.getDonorName(),
                    donation.getAmount().toString(),
                    dateStr);
            donation.setReceiptPath(path);
            donationRepository.save(donation);
            byte[] regenerated = pdfUtil.readFileBytes(path);
            if (regenerated == null) {
                return pdfUtil.buildDonationReceiptBytes(
                        donation.getDonationId(),
                        donation.getDonorName(),
                        donation.getAmount().toString(),
                        dateStr);
            }
            return regenerated;
        } catch (Exception e) {
            log.error("Failed to load receipt for {}", donationId, e);
            throw new BadRequestException("Unable to download receipt");
        }
    }

    @Transactional
    public byte[] getQrImage(String donationId) {
        Donation donation = donationRepository.findByDonationId(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        try {
            if (donation.getQrCodePath() == null) {
                String qrPath = qrCodeUtil.generateQrCode("DONATION:" + donationId, donationId);
                donation.setQrCodePath(qrPath);
                donationRepository.save(donation);
            }
            byte[] image = pdfUtil.readFileBytes(donation.getQrCodePath());
            if (image == null) {
                throw new ResourceNotFoundException("QR code not found");
            }
            return image;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to load QR for {}", donationId, e);
            throw new BadRequestException("Unable to download QR code");
        }
    }

    public DonationResponse mapToResponse(Donation donation) {
        return DonationResponse.builder()
                .id(donation.getId())
                .donationId(donation.getDonationId())
                .amount(donation.getAmount())
                .donorName(donation.getIsAnonymous() ? "Anonymous" : donation.getDonorName())
                .donorEmail(donation.getDonorEmail())
                .status(donation.getStatus())
                .campaignTitle(donation.getCampaign() != null ? donation.getCampaign().getTitle() : null)
                .campaignId(donation.getCampaign() != null ? donation.getCampaign().getId() : null)
                .qrCodePath(donation.getQrCodePath())
                .receiptPath(donation.getReceiptPath())
                .message(donation.getMessage())
                .createdAt(donation.getCreatedAt())
                .build();
    }
}
