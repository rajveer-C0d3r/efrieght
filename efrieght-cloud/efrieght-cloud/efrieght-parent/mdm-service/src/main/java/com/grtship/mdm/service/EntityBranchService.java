package com.grtship.mdm.service;

import java.util.List;

import javax.validation.Valid;

import com.grtship.core.dto.BranchContactDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.EntityBranchTaxDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.mdm.domain.ExternalEntity;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.EntityBranch}.
 */
public interface EntityBranchService {

	/**
	 * Save a branchDetails.
	 *
	 * @param branchDetailsDTO the entity to save.
	 * @return the persisted entity.
	 */
	EntityBranchDTO save(@Valid EntityBranchRequestDTO branchDetailsDTO);

	/**
	 * Get the "id" branchDetails.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */

	EntityBranchDTO saveDefaultBranch(ExternalEntity entityObj);

	EntityBranchDTO deactivate(@Valid DeactivationDTO deactivateDto);
	
	EntityBranchDTO reactivate(@Valid ReactivationDTO reactivateDto);

	List<EntityBranchTaxDTO> saveBranchTax(@Valid List<EntityBranchTaxDTO> branchTaxDto);

	List<BranchContactDTO> saveBranchContacts(@Valid List<BranchContactDTO> branchContactDtos);

	EntityBranchDTO update(@Valid EntityBranchDTO branchDetailsDTO);

}
