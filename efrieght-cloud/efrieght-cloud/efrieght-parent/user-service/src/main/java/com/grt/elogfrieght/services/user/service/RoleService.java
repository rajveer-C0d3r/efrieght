package com.grt.elogfrieght.services.user.service;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.RoleDTO;

import javassist.tools.rmi.ObjectNotFoundException;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Role}.
 */
public interface RoleService {

	/**
	 * Save a role.
	 *
	 * @param roleDTO the entity to save.
	 * @return the persisted entity.
	 * @throws InvalidDataException 
	 */
	RoleDTO save(RoleDTO roleDTO) throws InvalidDataException;

	/**
	 * Delete the "id" role.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	RoleDTO update(RoleDTO roleDTO) throws ObjectNotFoundException, InvalidDataException;
	
	RoleDTO deactivate(DeactivationDTO deactivateDto);

	RoleDTO reactivate(ReactivationDTO reactivateDto);
}
