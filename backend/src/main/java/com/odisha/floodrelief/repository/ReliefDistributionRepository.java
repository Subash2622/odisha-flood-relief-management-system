package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.ReliefDistribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReliefDistributionRepository extends JpaRepository<ReliefDistribution, Long> {

    Page<ReliefDistribution> findByVolunteerId(Long volunteerId, Pageable pageable);
}
