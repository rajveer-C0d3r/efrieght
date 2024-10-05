package com.grtship.client.service;

import java.util.List;

import javax.validation.Valid;

import com.grtship.core.dto.CompanyBranchCreationDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;

/**
 * Service Interface for managing
 * {@link com.grtship.efreight.client.domain.CompanyBranch}.
 */
public interface CompanyBranchService {

	/**
	 * Save a companyBranch.
	 *
	 * @param companyBranchDTO the entity to save.
	 * @return the persisted entity.
	 */
	CompanyBranchDTO save(CompanyBranchCreationDTO companyBranchDto);

	CompanyBranchDTO update(@Valid CompanyBranchDTO companyBranchDto);

	CompanyBranchDTO deactivate(@Valid DeactivationDTO deactivateDto);

	CompanyBranchDTO reactivate(@Valid ReactivationDTO reactivateDto);

	UserSpecificCBResponse getUserSpecificCompanyBranchResponseByClientId(Long clientId);

	UserSpecificCBResponse getUserSpecificCompanyBranchDetails(UserSpecificCBRequest cbRequest);
	
	List<UserSpecificBranchDTO> getUserSpecificBranchDetails(UserAccessCompanyBranchDTO companyBranchDTO);
}
