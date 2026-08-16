package com.odisha.floodrelief.config;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.*;
import com.odisha.floodrelief.entity.enums.ReliefItemType;
import com.odisha.floodrelief.entity.enums.RoleName;
import com.odisha.floodrelief.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InventoryRepository inventoryRepository;
    private final OrganizationDetailsRepository organizationDetailsRepository;
    private final DistrictRepository districtRepository;

    @Override
    public void run(String... args) {
        initRoles();
        initCeoUser();
        initInventory();
        initOrganization();
        initDistricts();
        log.info("Data initialization completed");
    }

    private void initRoles() {
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (!roleRepository.findByName(roleName).isPresent()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        });
    }

    private void initCeoUser() {
        if (!userRepository.existsByUsername("ceo")) {
            Role ceoRole = roleRepository.findByName(RoleName.ROLE_CEO)
                    .orElseThrow(() -> new RuntimeException("CEO role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(ceoRole);

            User ceo = User.builder()
                    .username("ceo")
                    .email("ceo@odishafloodrelief.org")
                    .password(passwordEncoder.encode("ceo123"))
                    .fullName("Chief Executive Officer")
                    .phone("9876543210")
                    .roles(roles)
                    .enabled(true)
                    .build();

            userRepository.save(ceo);
            log.info("Default CEO user created: ceo / ceo123");
        }
    }

    private void initInventory() {
        Arrays.stream(ReliefItemType.values()).forEach(itemType -> {
            if (!inventoryRepository.findByItemType(itemType).isPresent()) {
                inventoryRepository.save(Inventory.builder()
                        .itemType(itemType)
                        .quantity(100)
                        .minThreshold(10)
                        .build());
            }
        });
    }

    private void initOrganization() {
        if (organizationDetailsRepository.count() == 0) {
            organizationDetailsRepository.save(OrganizationDetails.builder()
                    .orgName("Odisha Flood Relief Foundation")
                    .description("A trusted NGO working for flood relief across Odisha")
                    .email("contact@odishafloodrelief.org")
                    .phone("+91-9876543210")
                    .address("Bhubaneswar, Odisha, India")
                    .bankName("State Bank of India")
                    .bankAccountNumber("123456789012")
                    .bankIfsc("SBIN0001234")
                    .upiId("odishafloodrelief@upi")
                    .build());
        }
    }

    private void initDistricts() {
        List<String> odishaDistricts = Arrays.asList(
                "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh",
                "Cuttack", "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur",
                "Jajpur", "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar",
                "Khordha", "Koraput", "Malkangiri", "Mayurbhanj", "Nabarangpur", "Nayagarh",
                "Nuapada", "Puri", "Rayagada", "Sambalpur", "Subarnapur", "Sundargarh"
        );

        odishaDistricts.forEach(name -> {
            if (!districtRepository.findByName(name).isPresent()) {
                districtRepository.save(District.builder().name(name).code(name.substring(0, 3).toUpperCase()).build());
            }
        });
    }
}
