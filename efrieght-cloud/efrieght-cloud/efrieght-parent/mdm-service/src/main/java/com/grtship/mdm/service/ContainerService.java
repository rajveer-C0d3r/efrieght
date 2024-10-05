package com.grtship.mdm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.ContainerDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Container}.
 */
public interface ContainerService {

	/**
	 * Save a container.
	 *
	 * @param containerDTO the entity to save.
	 * @return the persisted entity.
	 */
	ContainerDTO save(ContainerDTO containerDTO);

	/**
	 * Get all the containers.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<ContainerDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" container.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ContainerDTO> findOne(Long id);

	/**
	 * Delete the "id" container.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
