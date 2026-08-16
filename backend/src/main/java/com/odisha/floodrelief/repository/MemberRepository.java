package com.odisha.floodrelief.repository;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.Member;
import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(Long userId);

    Optional<Member> findByMembershipId(String membershipId);

    Page<Member> findByStatus(ApprovalStatus status, Pageable pageable);

    long countByStatus(ApprovalStatus status);
}
