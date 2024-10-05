package com.grtship.mdm.service;

import com.grtship.core.dto.DestinationDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Destination}.
 */
public interface DestinationService {

	/**
	 * Save a destination.
	 *
	 * @param destinationDTO the entity to save.
	 * @return the persisted entity.
	 */
	DestinationDTO save(DestinationDTO destinationDTO);

	DestinationDTO update(DestinationDTO destinationDto);

	/**
	 * Delete the "id" destination.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

}
