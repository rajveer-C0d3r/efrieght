package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.MainModuleDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.MainModule}.
 */
public interface MainModuleService {

	/**
	 * Get all the mainModules.
	 *
	 * @return the list of entities.
	 */
	List<MainModuleDTO> findAll();

	/**
	 * Get the "id" mainModule.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<MainModuleDTO> findOne(Long id);
}
