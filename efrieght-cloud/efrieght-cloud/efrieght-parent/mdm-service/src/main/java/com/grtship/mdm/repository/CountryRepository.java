package com.grtship.mdm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Country;

/**
 * Spring Data  repository for the Country entity.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {

	String findByName(String id);

	@Query("select c.sector.id from Country c where c.id = ?1")
	Long getSectorIdByCountryId(Long countryId);

	@Query("select c.isStateMandatory from Country c where  c.id =?1")
	Boolean getIsStateMandatoryById(Long countryId);

	List<Country> findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	List<Country> findByNameAndClientIdAndCompanyId(String name, Long clientId, Long companyId);
	
}
