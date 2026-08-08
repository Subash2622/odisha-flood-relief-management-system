package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.FloodReport;
import com.odisha.floodrelief.entity.enums.FloodReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloodReportRepository extends JpaRepository<FloodReport, Long> {

    Page<FloodReport> findByStatus(FloodReportStatus status, Pageable pageable);

    long countByStatus(FloodReportStatus status);

    @Query("SELECT f.district, COUNT(f) FROM FloodReport f GROUP BY f.district")
    List<Object[]> countByDistrict();
}
