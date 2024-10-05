package com.grtship.client.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.client.criteria.CompanyBranchCriteria;
import com.grtship.core.dto.CompanyBranchBaseDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.UserCompanyDTO;

public interface CompanyBranchQueryService {

	public List<CompanyBranchDTO> findByCriteria(CompanyBranchCriteria criteria);

	public Page<CompanyBranchDTO> findByCriteria(CompanyBranchCriteria criteria, Pageable page);

	/**
	 * Get the "id" companyBranch.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	public Optional<CompanyBranchDTO> findOne(Long id);

	List<CompanyBranchBaseDTO> findByCompanyIdForMultiDropdown(List<Long> companyIdList);

	List<CompanyBranchBaseDTO> findByIdForMultiDropdown(List<Long> branchIdList);

	List<Long> getBranchIdsByCompanyIdAndActiveFlag(Long companyId, Boolean activeFlag);

	List<Long> getBranchIdsByCompanyIdAndActiveFlagAndDeactivateAutoGenerateId(Long companyid, Boolean activeFlag,
			String deactivateAutoGenerateId);

	List<UserCompanyDTO> findByCompanyIdWithAllBranches(List<Long> companyIdList);

	List<UserCompanyDTO> findByIdWithSpecificBranches(List<Long> branchIdList);
	
	List<UserCompanyDTO> getByCompanyIdWithAllBranches(List<Long> companyIdList);
}
