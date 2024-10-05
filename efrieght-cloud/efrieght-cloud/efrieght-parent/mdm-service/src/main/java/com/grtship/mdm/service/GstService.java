package com.grtship.mdm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.GstDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Gst}.
 */
public interface GstService {

	/**
	 * Save a gst.
	 *
	 * @param gstDTO the entity to save.
	 * @return the persisted entity.
	 */
	GstDTO save(GstDTO gstDTO);

	/**
	 * Get all the gsts.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<GstDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" gst.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<GstDTO> findOne(Long id);

	/**
	 * Delete the "id" gst.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
