package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Equipment;

/**
 * Spring Data  repository for the Equipment entity.
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
