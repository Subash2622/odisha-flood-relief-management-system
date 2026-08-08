package com.odisha.floodrelief.service;

import com.odisha.floodrelief.dto.request.ReliefDistributionRequest;
import com.odisha.floodrelief.dto.response.ReliefDistributionResponse;
import com.odisha.floodrelief.entity.*;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.*;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReliefService {

    private final ReliefDistributionRepository reliefDistributionRepository;
    private final InventoryRepository inventoryRepository;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;

    @Transactional
    public ReliefDistributionResponse distribute(ReliefDistributionRequest request, MultipartFile campPhoto) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Inventory inventory = inventoryRepository.findByItemType(request.getItemType())
                .orElseThrow(() -> new BadRequestException("Inventory item not found"));

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient inventory for " + request.getItemType());
        }

        Volunteer volunteer = null;
        if (request.getVolunteerId() != null) {
            volunteer = volunteerRepository.findById(request.getVolunteerId()).orElse(null);
        }

        ReliefDistribution distribution = ReliefDistribution.builder()
                .volunteer(volunteer)
                .distributedBy(user)
                .itemType(request.getItemType())
                .quantity(request.getQuantity())
                .village(request.getVillage())
                .district(request.getDistrict())
                .notes(request.getNotes())
                .distributionCompleted(true)
                .build();

        if (campPhoto != null && !campPhoto.isEmpty()) {
            distribution.setCampPhoto(fileStorageUtil.storeFile(campPhoto, "relief-camps"));
        }

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventoryRepository.save(inventory);

        return toResponse(reliefDistributionRepository.save(distribution));
    }

    @Transactional(readOnly = true)
    public Page<ReliefDistributionResponse> getDistributionHistory(Pageable pageable) {
        return reliefDistributionRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReliefDistributionResponse> getMyDistributions(Pageable pageable) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Volunteer volunteer = volunteerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found"));
        return reliefDistributionRepository.findByVolunteerId(volunteer.getId(), pageable).map(this::toResponse);
    }

    private ReliefDistributionResponse toResponse(ReliefDistribution d) {
        String volunteerName = null;
        if (d.getVolunteer() != null && d.getVolunteer().getUser() != null) {
            volunteerName = d.getVolunteer().getUser().getFullName();
        }
        String distributedByName = d.getDistributedBy() != null ? d.getDistributedBy().getFullName() : null;
        return ReliefDistributionResponse.builder()
                .id(d.getId())
                .itemType(d.getItemType())
                .quantity(d.getQuantity())
                .village(d.getVillage())
                .district(d.getDistrict())
                .campPhoto(d.getCampPhoto())
                .notes(d.getNotes())
                .distributionCompleted(d.getDistributionCompleted())
                .volunteerName(volunteerName)
                .distributedByName(distributedByName)
                .createdAt(d.getCreatedAt())
                .build();
    }
}
