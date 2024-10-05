package com.grtship.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.grtship.mdm.domain.Destination;

/**
 * Spring Data repository for the Destination entity.
 */
@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long>, JpaSpecificationExecutor<Destination> {

	Destination findByCodeAndClientIdAndCompanyId(String code, Long clientId, Long companyId);

	Destination findByCodeAndIdNotAndClientIdAndCompanyId(String code, Long id, Long clientId, Long companyId);

	Destination findByNameAndCountryIdAndClientIdAndCompanyId(String name, Long countryId, Long clientId,
			Long companyId);

	Destination findByNameAndIdNotAndCountryIdAndClientIdAndCompanyId(String name, Long id, Long countryId, Long clientId,
			Long companyId);

}
