package com.grtship.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grtship.account.domain.ShipmentReference;

/**
 * Spring Data repository for the ShipmentReference entity.
 */
@Repository
public interface ShipmentReferenceRepository extends JpaRepository<ShipmentReference, Long> {
}
