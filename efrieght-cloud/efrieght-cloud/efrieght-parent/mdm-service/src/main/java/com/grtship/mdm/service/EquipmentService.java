package com.grtship.mdm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.EquipmentDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Equipment}.
 */
public interface EquipmentService {

	/**
	 * Save a equipment.
	 *
	 * @param equipmentDTO the entity to save.
	 * @return the persisted entity.
	 */
	EquipmentDTO save(EquipmentDTO equipmentDTO);

	/**
	 * Get all the equipment.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<EquipmentDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" equipment.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<EquipmentDTO> findOne(Long id);

	/**
	 * Delete the "id" equipment.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
