package com.grtship.client.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.Company;
import com.grtship.core.dto.CompanyBranchBaseDTO;

/**
 * Spring Data repository for the Company entity.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

	@Query("select c from Company c where c.client.id in ?1")
	List<Company> findByClientIdIn(List<Long> clientIds);

	List<Company> findByCodeAndClientId(String code, Long clientId);

	List<Company> findByNameAndClientIdAndIdNot(String name, Long clientId, Long id);

	List<Company> findByWithholdingTaxId(String withholdingTaxId);

	List<Company> findByGstNo(String gstNo);

	List<Company> findByPanNo(String panNo);

	List<Company> findByNameAndClientIdAndAddressCountryId(String name, Long clientId, Long countryId);
 
	@Query("select new com.grtship.core.dto.CompanyBranchBaseDTO(c.id, c.code, c.id AS companyId, c.name, c.client.id) "
			+ "from Company c "
			+ "where c.id in ?1 and c.id not in "
			+ "  ( select cb.company.id from CompanyBranch cb)")
	List<CompanyBranchBaseDTO> getCompaniesByIds(List<Long> companyIdList);

	List<Company> findByClientId(Long clientId);

}
