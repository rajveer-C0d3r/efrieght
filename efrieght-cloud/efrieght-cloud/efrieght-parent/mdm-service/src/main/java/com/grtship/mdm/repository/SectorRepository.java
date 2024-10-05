package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Sector;

/**
 * Spring Data  repository for the Sector entity.
 */
@Repository
public interface SectorRepository extends JpaRepository<Sector, Long>, JpaSpecificationExecutor<Sector> {

	List<Sector> findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	List<Sector> findByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);

	
}
