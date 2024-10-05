package com.grt.elogfrieght.services.user.service;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.dto.PermissionDTO;

/**
 * Service Interface for managing {@link com.grt.oath2.domain.Permission}.
 */
public interface PermissionService {

	/**
	 * Save a permission.
	 *
	 * @param permissionDTO the entity to save.
	 * @return the persisted entity.
	 */
	PermissionDTO save(PermissionDTO permissionDTO);

	/**
	 * Delete the "id" permission.
	 *
	 * @param id the id of the entity.
	 */
	void delete(String permissionCode);

	PermissionDTO update(PermissionDTO permissionDTO) throws InvalidDataException;
}
