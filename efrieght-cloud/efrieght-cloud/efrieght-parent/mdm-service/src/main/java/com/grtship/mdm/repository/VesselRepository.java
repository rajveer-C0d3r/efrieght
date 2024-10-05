package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Vessel;

/**
 * Spring Data  repository for the Vessel entity.
 */
@Repository
public interface VesselRepository extends JpaRepository<Vessel, Long>, JpaSpecificationExecutor<Vessel> {
}
