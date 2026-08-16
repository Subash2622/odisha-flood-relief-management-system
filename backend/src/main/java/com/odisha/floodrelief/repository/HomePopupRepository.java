package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.HomePopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomePopupRepository extends JpaRepository<HomePopup, Long> {

    List<HomePopup> findByIsActiveTrueOrderByPriorityDescCreatedAtDesc();

    List<HomePopup> findAllByOrderByCreatedAtDesc();
}
