package com.odisha.floodrelief.repository;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.OrganizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationDetailsRepository extends JpaRepository<OrganizationDetails, Long> {
}
