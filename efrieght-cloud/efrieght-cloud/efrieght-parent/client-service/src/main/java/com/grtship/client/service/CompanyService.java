package com.grtship.client.service;

import java.util.List;

import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.UserAccessCompanyBranchDTO;
import com.grtship.core.dto.UserSpecificCBRequest;
import com.grtship.core.dto.UserSpecificCBResponse;
import com.grtship.core.dto.UserSpecificCompanyDTO;

/**
 * Service Interface for managing
 * {@link com.grtship.efreight.client.domain.Company}.
 */
public interface CompanyService {

	/**
	 * Save a company.
	 *
	 * @param companyDTO the entity to save.
	 * @return the persisted entity.
	 * @throws Exception
	 */
	CompanyDTO save(CompanyDTO companyDTO) throws Exception;

	CompanyDTO update(CompanyDTO companyDto) throws Exception;

	/**
	 * Get the "id" company.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */

	CompanyDTO deactivate(DeactivationDTO deactivateDto);

	CompanyDTO reactivate(ReactivationDTO activateDto);

	UserSpecificCBResponse userSpecificCBDetails(UserSpecificCBRequest cbRequest);
	
	List<UserSpecificCompanyDTO> userSpecificCompanyDetails(UserAccessCompanyBranchDTO companyBranchDTO);
}
