package com.grtship.account.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.ShipmentReferenceDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.ShipmentReference}.
 */
public interface ShipmentReferenceService {
	/**
	 * Save a ShipmentReference.
	 *
	 * @param ShipmentReferenceDTO the entity to save.
	 * @return the persisted entity.
	 */
	ShipmentReferenceDTO save(ShipmentReferenceDTO shipmentReferenceDto);

	/**
	 * Get all the ShipmentReferences.
	 *
	 */
	List<ShipmentReferenceDTO> findAll();

	/**
	 * Get the "id" ShipmentReference.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ShipmentReferenceDTO> findOne(Long id);

	/**
	 * Delete the "id" ShipmentReference.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
