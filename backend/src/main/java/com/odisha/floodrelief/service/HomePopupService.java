package com.odisha.floodrelief.service;

import com.odisha.floodrelief.dto.request.HomePopupRequest;
import com.odisha.floodrelief.dto.response.HomePopupResponse;
import com.odisha.floodrelief.entity.HomePopup;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.HomePopupRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomePopupService {

    private final HomePopupRepository homePopupRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<HomePopupResponse> getActivePopups() {
        return homePopupRepository.findByIsActiveTrueOrderByPriorityDescCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomePopupResponse> getAllPopups() {
        return homePopupRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HomePopupResponse createPopup(HomePopupRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        HomePopup popup = HomePopup.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .isActive(true)
                .createdBy(user)
                .build();

        return toResponse(homePopupRepository.save(popup));
    }

    @Transactional
    public HomePopupResponse toggleActive(Long id) {
        HomePopup popup = homePopupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Popup not found"));
        popup.setIsActive(!Boolean.TRUE.equals(popup.getIsActive()));
        return toResponse(homePopupRepository.save(popup));
    }

    @Transactional
    public void deletePopup(Long id) {
        if (!homePopupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Popup not found");
        }
        homePopupRepository.deleteById(id);
    }

    private HomePopupResponse toResponse(HomePopup popup) {
        return HomePopupResponse.builder()
                .id(popup.getId())
                .title(popup.getTitle())
                .message(popup.getMessage())
                .type(popup.getType())
                .isActive(popup.getIsActive())
                .priority(popup.getPriority())
                .createdByName(popup.getCreatedBy() != null ? popup.getCreatedBy().getFullName() : null)
                .createdAt(popup.getCreatedAt())
                .build();
    }
}
