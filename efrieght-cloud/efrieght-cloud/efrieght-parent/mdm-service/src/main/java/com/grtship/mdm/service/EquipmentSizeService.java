package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.EquipmentSizeDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.EquipmentSize}.
 */
public interface EquipmentSizeService {

	/**
	 * Save a equipmentSize.
	 *
	 * @param equipmentSizeDTO the entity to save.
	 * @return the persisted entity.
	 */
	EquipmentSizeDTO save(EquipmentSizeDTO equipmentSizeDTO);

	/**
	 * Get all the equipmentSizes.
	 *
	 * @return the list of entities.
	 */
	List<EquipmentSizeDTO> findAll();

	/**
	 * Get the "id" equipmentSize.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<EquipmentSizeDTO> findOne(Long id);

	/**
	 * Delete the "id" equipmentSize.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
