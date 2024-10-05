package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.EquipmentSize;

/**
 * Spring Data  repository for the EquipmentSize entity.
 */
@Repository
public interface EquipmentSizeRepository extends JpaRepository<EquipmentSize, Long> {
}
