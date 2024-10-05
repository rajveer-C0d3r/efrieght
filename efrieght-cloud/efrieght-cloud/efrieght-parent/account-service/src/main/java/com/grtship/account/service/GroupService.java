package com.grtship.account.service;

import java.io.IOException;

import com.grtship.core.dto.AuthorizationObjectDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GroupCreationDTO;
import com.grtship.core.dto.GroupDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.account.domain.Group}.
 */
public interface GroupService {

	/**
	 * Save a group.
	 *
	 * @param groupDTO the entity to save.
	 * @return the persisted entity.
	 */
	GroupDTO save(GroupCreationDTO groupDTO);

	GroupDTO update(GroupDTO groupDto);

	/**
	 * Delete the "id" group.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	GroupDTO deactivate(DeactivationDTO deactivateDto);

	GroupDTO reactivate(ReactivationDTO reactivationDto);

	void createBaseGroups(Long companyId) throws IOException;
	
	Boolean approveGroup(AuthorizationObjectDTO authorizationObjectDTO);
}
