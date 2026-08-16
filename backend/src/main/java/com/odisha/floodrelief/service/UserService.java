package com.odisha.floodrelief.service;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.dto.response.UserResponse;
import com.odisha.floodrelief.entity.Role;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.entity.enums.RoleName;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.RoleRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.AuditLogUtil;
import com.odisha.floodrelief.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageUtil fileStorageUtil;
    private final AuditLogUtil auditLogUtil;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(authService::mapToUserResponse);
    }

    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable).map(authService::mapToUserResponse);
    }

    public UserResponse getUserById(Long id) {
        return authService.mapToUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional
    public UserResponse updateProfile(String fullName, String phone, String address, MultipartFile profileImage) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (fullName != null) user.setFullName(fullName);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        if (profileImage != null && !profileImage.isEmpty()) {
            user.setProfileImage(fileStorageUtil.storeFile(profileImage, "profiles"));
        }

        return authService.mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse createAdmin(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already taken");
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User admin = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .roles(roles)
                .enabled(true)
                .build();

        admin = userRepository.save(admin);
        auditLogUtil.log(admin, "CREATE_ADMIN", "User", admin.getId(), "Admin created");
        return authService.mapToUserResponse(admin);
    }

    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isCeo = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_CEO);
        if (isCeo) {
            throw new BadRequestException("Cannot disable CEO account");
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isCeo = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_CEO);
        if (isCeo) {
            throw new BadRequestException("Cannot delete CEO account");
        }

        userRepository.delete(user);
    }
}
