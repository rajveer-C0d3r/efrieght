package com.grtship.client.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grtship.client.domain.Company;
import com.grtship.client.domain.CompanyBranch;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.UserCompanyDTO;
import com.grtship.core.enumeration.DomainStatus;

/**
 * Spring Data repository for the CompanyBranch entity.
 */
@Repository
public interface CompanyBranchRepository
		extends JpaRepository<CompanyBranch, Long>, JpaSpecificationExecutor<CompanyBranch> {

	@Query("select new com.grtship.core.dto.CompanyBranchBaseDTO(cb.id, cb.code, cb.name, cb.company.id AS companyId, c.name, cb.clientId) "
			+ "from CompanyBranch cb " + "left join cb.company c on c.id = cb.company.id "
			+ "where cb.company.id in ?1")
	List<CompanyBranchBaseDTO> findByCompanyIdForMultiDropdown(List<Long> companyIdList);

	List<CompanyBranch> findByCompany_IdIn(List<Long> companyIds);

	@Query("select new com.grtship.core.dto.CompanyBranchBaseDTO(cb.id, cb.code, cb.name, cb.company.id AS companyId, c.name, cb.clientId) "
			+ "from CompanyBranch cb " + "left join cb.company c on c.id = cb.company.id " + "where cb.id in ?1")
	List<CompanyBranchBaseDTO> findByIdForMultiDropdown(List<Long> branchIdList);

	Optional<CompanyBranch> findByCodeAndClientId(String code, Long clientId);

	Optional<CompanyBranch> findByNameAndClientId(String name, Long clientId);

	Optional<CompanyBranch> findByCodeAndClientIdAndIdNot(String code, Long clientId, Long id);

	Optional<CompanyBranch> findByNameAndClientIdAndIdNot(String name, Long clientId, Long id);

	@Query("select cb.id from CompanyBranch cb where cb.company.id = ?1 and cb.activeFlag = ?2")
	List<Long> findByCompanyIdAndActiveFlag(Long companyId, Boolean activeFlag);

	@Query("select cb.id from CompanyBranch cb where cb.company.id = ?1 and cb.activeFlag = ?2 and cb.deactivateDtls.deactivateAutoGenerateId = ?3")
	List<Long> findByCompanyIdAndActiveFlagAndDeactivateDtlsDeactivateAutoGenerateId(Long companyId, Boolean activeFlag,
			String deactivateAutoGenerateId);

	Optional<CompanyBranch> findByIdAndStatus(Long id, DomainStatus pending);

	@Query("select new com.grtship.core.dto.UserCompanyDTO(cb.id, cb.name, cb.company.id AS companyId, c.name, true) "
			+ "from CompanyBranch cb " + "left join cb.company c on c.id = cb.company.id "
			+ "where cb.company.id in ?1")
	List<UserCompanyDTO> findByCompanyIdWithAllBranches(List<Long> companyIdList);

	@Query("select new com.grtship.core.dto.UserCompanyDTO(cb.id, cb.name, cb.company.id AS companyId, c.name, false) "
			+ "from CompanyBranch cb " + "left join cb.company c on c.id = cb.company.id " + "where cb.id in ?1")
	List<UserCompanyDTO> findByIdWithSpecificBranches(List<Long> branchIdList);

	List<CompanyBranch> findByCompany(Company companyId);

	List<CompanyBranch> findByIdIn(List<Long> branchIdList);

	// this query will be called when user have all companies access true
	List<CompanyBranch> findByClientId(Long clientId);

	// this query will be called when user with specific company have all branches
	// access true
	List<CompanyBranch> findByClientIdAndCompany_IdIn(Long clientId, List<Long> companyIds);

	// this query will be called when user with specific company have specific
	// branch access
	List<CompanyBranch> findByClientIdAndCompany_IdInAndIdIn(Long clientId, List<Long> companyIds, List<Long> branchIds);
	
	@Query("select new com.grtship.core.dto.UserCompanyDTO(cb.company.id AS companyId, c.name, true) "
			+ "from CompanyBranch cb " + "left join cb.company c on c.id = cb.company.id "
			+ "where cb.company.id in ?1")
	List<UserCompanyDTO> getByCompanyIdWithAllBranches(List<Long> companyIdList);

	List<CompanyBranch> findByCompany_Id(Long companyId);

}
