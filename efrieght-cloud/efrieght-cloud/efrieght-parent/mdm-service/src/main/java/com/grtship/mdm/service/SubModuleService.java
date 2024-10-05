package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.SubModuleDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.SubModule}.
 */
public interface SubModuleService {

	/**
	 * Get all the subModules.
	 *
	 * @return the list of entities.
	 */
	List<SubModuleDTO> findAll();

	/**
	 * Get the "id" subModule.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<SubModuleDTO> findOne(Long id);

	List<SubModuleDTO> findByMainModuleId(Long mainModuleId);
}
