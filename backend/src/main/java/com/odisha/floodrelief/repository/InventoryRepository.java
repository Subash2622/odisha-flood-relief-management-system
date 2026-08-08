package com.odisha.floodrelief.repository;

import com.odisha.floodrelief.entity.Inventory;
import com.odisha.floodrelief.entity.enums.ReliefItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItemType(ReliefItemType itemType);
}
