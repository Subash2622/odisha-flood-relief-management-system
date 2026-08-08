package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.Volunteer;
import com.odisha.floodrelief.entity.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByUserId(Long userId);

    Page<Volunteer> findByStatus(ApprovalStatus status, Pageable pageable);

    long countByStatus(ApprovalStatus status);
}
