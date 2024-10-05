package com.grtship.mdm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.ObjectApprovalDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.domain.ObjectApproval}.
 */
public interface ObjectApprovalService {

	/**
	 * Save a objectApproval.
	 *
	 * @param objectApprovalDTO the entity to save.
	 * @return the persisted entity.
	 */
	ObjectApprovalDTO save(ObjectApprovalDTO objectApprovalDTO);

	/**
	 * Get all the objectApprovals.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<ObjectApprovalDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" objectApproval.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ObjectApprovalDTO> findOne(Long id);

	/**
	 * Delete the "id" objectApproval.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
