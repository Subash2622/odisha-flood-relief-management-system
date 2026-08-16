package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.VolunteerResponse;
import com.odisha.floodrelief.entity.*;
import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import com.odisha.floodrelief.entity.enums.RoleName;
import com.odisha.floodrelief.entity.enums.WorkStatus;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.*;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.AuditLogUtil;
import com.odisha.floodrelief.util.FileStorageUtil;
import com.odisha.floodrelief.util.IdGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;
    private final IdGeneratorUtil idGeneratorUtil;
    private final FileStorageUtil fileStorageUtil;
    private final AuditLogUtil auditLogUtil;

    @Transactional
    public VolunteerResponse applyVolunteer() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (volunteerRepository.findByUserId(user.getId()).isPresent()) {
            throw new BadRequestException("Volunteer application already exists");
        }

        Volunteer volunteer = Volunteer.builder()
                .user(user)
                .status(ApprovalStatus.PENDING)
                .build();

        Volunteer saved = volunteerRepository.save(volunteer);
        log.info("VOLUNTEER applied: user={}", user.getUsername());
        return mapToResponse(saved);
    }

    @Transactional
    public VolunteerResponse approveVolunteer(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));

        volunteer.setStatus(ApprovalStatus.APPROVED);
        volunteer.setVolunteerId(idGeneratorUtil.generateVolunteerId());

        Role volunteerRole = roleRepository.findByName(RoleName.ROLE_VOLUNTEER)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer role not found"));
        User user = volunteer.getUser();
        user.getRoles().add(volunteerRole);
        userRepository.save(user);

        volunteer = volunteerRepository.save(volunteer);
        log.info("VOLUNTEER approved: volunteerId={} user={}", volunteer.getVolunteerId(), user.getUsername());

        notificationRepository.save(Notification.builder()
                .user(user)
                .title("Volunteer Approved")
                .message("Your volunteer application has been approved.")
                .type("VOLUNTEER")
                .build());

        return mapToResponse(volunteer);
    }

    @Transactional
    public VolunteerResponse assignVolunteer(Long volunteerId, String area, String district) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));

        volunteer.setAssignedArea(area);
        volunteer.setAssignedDistrict(district);
        volunteer.setWorkStatus(WorkStatus.ASSIGNED);

        notificationRepository.save(Notification.builder()
                .user(volunteer.getUser())
                .title("Work Assigned")
                .message("You have been assigned to " + area + ", " + district)
                .type("VOLUNTEER")
                .build());

        return mapToResponse(volunteerRepository.save(volunteer));
    }

    @Transactional
    public VolunteerResponse updateWorkStatus(WorkStatus status, String notes) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Volunteer volunteer = volunteerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found"));

        volunteer.setWorkStatus(status);
        if (notes != null) {
            volunteer.setNotes(notes);
        }
        return mapToResponse(volunteerRepository.save(volunteer));
    }

    @Transactional
    public VolunteerResponse uploadPhotos(MultipartFile before, MultipartFile after) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Volunteer volunteer = volunteerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found"));

        if (before != null && !before.isEmpty()) {
            volunteer.setBeforePhoto(fileStorageUtil.storeFile(before, "volunteer-photos"));
        }
        if (after != null && !after.isEmpty()) {
            volunteer.setAfterPhoto(fileStorageUtil.storeFile(after, "volunteer-photos"));
        }
        return mapToResponse(volunteerRepository.save(volunteer));
    }

    public VolunteerResponse getMyVolunteerProfile() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return volunteerRepository.findByUserId(principal.getId())
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found"));
    }

    public Page<VolunteerResponse> getAllVolunteers(Pageable pageable) {
        return volunteerRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<VolunteerResponse> getPendingVolunteers(Pageable pageable) {
        return volunteerRepository.findByStatus(ApprovalStatus.PENDING, pageable).map(this::mapToResponse);
    }

    private VolunteerResponse mapToResponse(Volunteer volunteer) {
        return VolunteerResponse.builder()
                .id(volunteer.getId())
                .volunteerId(volunteer.getVolunteerId())
                .fullName(volunteer.getUser().getFullName())
                .email(volunteer.getUser().getEmail())
                .phone(volunteer.getUser().getPhone())
                .status(volunteer.getStatus())
                .assignedArea(volunteer.getAssignedArea())
                .assignedDistrict(volunteer.getAssignedDistrict())
                .workStatus(volunteer.getWorkStatus())
                .beforePhoto(volunteer.getBeforePhoto())
                .afterPhoto(volunteer.getAfterPhoto())
                .notes(volunteer.getNotes())
                .createdAt(volunteer.getCreatedAt())
                .build();
    }
}
