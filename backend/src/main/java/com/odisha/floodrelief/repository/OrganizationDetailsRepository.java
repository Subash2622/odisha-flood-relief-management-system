package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.OrganizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationDetailsRepository extends JpaRepository<OrganizationDetails, Long> {
}
