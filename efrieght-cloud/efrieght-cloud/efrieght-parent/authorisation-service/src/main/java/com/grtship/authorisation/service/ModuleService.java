package com.grtship.authorisation.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.ModuleDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Module}.
 */
public interface ModuleService {

	/**
	 * Save a module.
	 *
	 * @param moduleDTO the entity to save.
	 * @return the persisted entity.
	 */
	ModuleDTO save(ModuleDTO moduleDTO);

	/**
	 * Get all the modules.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<ModuleDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" module.
	 *
	 * @param moduleName the id of the entity.
	 * @return the entity.
	 */
	Optional<ModuleDTO> findOne(String moduleName);

	/**
	 * Delete the "id" module.
	 *
	 * @param moduleName the id of the entity.
	 */
	void delete(String moduleName);
}
