package com.odisha.floodrelief.service;

import com.odisha.floodrelief.dto.request.LoginRequest;
import com.odisha.floodrelief.dto.request.RegisterRequest;
import com.odisha.floodrelief.dto.response.JwtResponse;
import com.odisha.floodrelief.dto.response.UserResponse;
import com.odisha.floodrelief.entity.Role;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.entity.enums.RoleName;
import com.odisha.floodrelief.exception.BadRequestException;
import com.odisha.floodrelief.exception.ResourceNotFoundException;
import com.odisha.floodrelief.repository.RoleRepository;
import com.odisha.floodrelief.repository.UserRepository;
import com.odisha.floodrelief.security.JwtTokenProvider;
import com.odisha.floodrelief.security.UserPrincipal;
import com.odisha.floodrelief.util.AuditLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogUtil auditLogUtil;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .roles(roles)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getUsername());
        return mapToUserResponse(user);
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditLogUtil.log(user, "LOGIN", "User", user.getId(), "User logged in");

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .build();
    }

    public JwtResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = tokenProvider.generateAccessTokenFromUsername(username);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .build();
    }

    public UserResponse getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    public UserResponse mapToUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImage(user.getProfileImage())
                .enabled(user.getEnabled())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
