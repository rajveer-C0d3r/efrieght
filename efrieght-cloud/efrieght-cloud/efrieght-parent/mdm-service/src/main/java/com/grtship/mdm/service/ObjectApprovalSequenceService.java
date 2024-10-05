package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import com.grtship.core.dto.ObjectApprovalSequenceDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.domain.ObjectApprovalSequence}.
 */
public interface ObjectApprovalSequenceService {

	/**
	 * Save a objectApprovalSequence.
	 *
	 * @param objectApprovalSequenceDTO the entity to save.
	 * @return the persisted entity.
	 */
	ObjectApprovalSequenceDTO save(ObjectApprovalSequenceDTO objectApprovalSequenceDTO);

	/**
	 * Get all the objectApprovalSequences.
	 *
	 * @return the list of entities.
	 */
	List<ObjectApprovalSequenceDTO> findAll();

	/**
	 * Get the "id" objectApprovalSequence.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ObjectApprovalSequenceDTO> findOne(Long id);

	/**
	 * Delete the "id" objectApprovalSequence.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
