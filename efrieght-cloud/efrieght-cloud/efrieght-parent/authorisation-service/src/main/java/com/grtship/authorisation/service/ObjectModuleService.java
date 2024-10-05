package com.grtship.authorisation.service;

import com.grtship.core.dto.AuthorizationContainerDTO;
import com.grtship.core.dto.ObjectModuleDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.ObjectModule}.
 */
public interface ObjectModuleService {

	/**
	 * Save a objectModule.
	 *
	 * @param objectModuleDTO the entity to save.
	 * @return the persisted entity.
	 */
	ObjectModuleDTO save(ObjectModuleDTO objectModuleDTO);

	/**
	 * Delete the "id" objectModule.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	ObjectModuleDTO update(ObjectModuleDTO objectModuleDTO);
	
	void getApprovalRequirement(AuthorizationContainerDTO containerDTO);
}
