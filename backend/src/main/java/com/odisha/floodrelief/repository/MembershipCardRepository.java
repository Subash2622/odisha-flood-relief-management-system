package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.MembershipCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipCardRepository extends JpaRepository<MembershipCard, Long> {

    Optional<MembershipCard> findByMemberId(Long memberId);
}
