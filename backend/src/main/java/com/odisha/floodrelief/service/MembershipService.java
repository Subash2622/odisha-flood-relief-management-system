package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.MemberResponse;
import com.odisha.floodrelief.entity.*;
import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import com.odisha.floodrelief.entity.enums.RoleName;
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

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MemberRepository memberRepository;
    private final MembershipCardRepository membershipCardRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;
    private final IdGeneratorUtil idGeneratorUtil;
    private final QrCodeUtil qrCodeUtil;
    private final PdfUtil pdfUtil;
    private final AuditLogUtil auditLogUtil;

    @Transactional
    public MemberResponse applyMembership() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (memberRepository.findByUserId(user.getId()).isPresent()) {
            throw new BadRequestException("Membership application already exists");
        }

        Member member = Member.builder()
                .user(user)
                .membershipId(idGeneratorUtil.generateMembershipId())
                .status(ApprovalStatus.PENDING)
                .build();

        Member saved = memberRepository.save(member);
        log.info("MEMBERSHIP applied: membershipId={} user={}", saved.getMembershipId(), user.getUsername());
        return mapToResponse(saved);
    }

    @Transactional
    public MemberResponse approveMembership(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.setStatus(ApprovalStatus.APPROVED);
        member.setValidFrom(LocalDate.now());
        member.setValidUntil(LocalDate.now().plusYears(1));

        String qrPath = qrCodeUtil.generateQrCode("MEMBER:" + member.getMembershipId(), member.getMembershipId());
        member.setQrCodePath(qrPath);

        Role memberRole = roleRepository.findByName(RoleName.ROLE_MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Member role not found"));
        User user = member.getUser();
        user.getRoles().add(memberRole);
        userRepository.save(user);

        member = memberRepository.save(member);
        log.info("MEMBERSHIP approved: membershipId={} user={}", member.getMembershipId(), user.getUsername());

        String pdfPath = pdfUtil.generateMembershipCard(
                member.getMembershipId(),
                user.getFullName(),
                member.getValidUntil().toString(),
                user.getProfileImage()
        );

        MembershipCard card = MembershipCard.builder()
                .member(member)
                .cardNumber(member.getMembershipId())
                .qrCodePath(qrPath)
                .pdfPath(pdfPath)
                .issuedDate(LocalDate.now())
                .expiryDate(member.getValidUntil())
                .build();
        membershipCardRepository.save(card);

        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Membership Approved")
                .message("Your membership has been approved. Membership ID: " + member.getMembershipId())
                .type("MEMBERSHIP")
                .build());

        auditLogUtil.log(user, "APPROVE_MEMBERSHIP", "Member", member.getId(), "Membership approved");
        return mapToResponse(member);
    }

    @Transactional
    public MemberResponse renewMembership() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (member.getStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Only approved members can renew");
        }

        member.setValidUntil(LocalDate.now().plusYears(1));
        member.setStatus(ApprovalStatus.PENDING);
        return mapToResponse(memberRepository.save(member));
    }

    public MemberResponse getMyMembership() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByUserId(principal.getId())
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
    }

    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<MemberResponse> getPendingMembers(Pageable pageable) {
        return memberRepository.findByStatus(ApprovalStatus.PENDING, pageable).map(this::mapToResponse);
    }

    public byte[] getMyMembershipCardPdf() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        if (member.getStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Membership card available only after approval");
        }
        try {
            MembershipCard card = membershipCardRepository.findByMemberId(member.getId()).orElse(null);
            // Always regenerate so latest profile photo/name are on the card
            String pdfPath = pdfUtil.generateMembershipCard(
                    member.getMembershipId(),
                    member.getUser().getFullName(),
                    member.getValidUntil() != null ? member.getValidUntil().toString() : "",
                    member.getUser().getProfileImage());
            if (card != null) {
                card.setPdfPath(pdfPath);
                membershipCardRepository.save(card);
            }
            byte[] bytes = pdfUtil.readFileBytes(pdfPath);
            if (bytes == null) {
                throw new BadRequestException("Unable to generate membership card");
            }
            return bytes;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unable to download membership card");
        }
    }

    private MemberResponse mapToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .membershipId(member.getMembershipId())
                .fullName(member.getUser().getFullName())
                .email(member.getUser().getEmail())
                .profileImage(member.getUser().getProfileImage())
                .status(member.getStatus())
                .validFrom(member.getValidFrom())
                .validUntil(member.getValidUntil())
                .qrCodePath(member.getQrCodePath())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
