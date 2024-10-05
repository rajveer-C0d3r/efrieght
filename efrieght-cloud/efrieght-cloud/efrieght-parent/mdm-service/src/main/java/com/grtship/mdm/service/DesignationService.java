package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.dto.ResponseCodeDTO;
import com.grtship.mdm.criteria.DesignationCriteria;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Designation}.
 */
public interface DesignationService {

	/**
	 * Save a designation.
	 *
	 * @param designationDTO the entity to save.
	 * @return the persisted entity.
	 */
	DesignationDTO save(DesignationDTO designationDTO);

	/**
	 * Get all the designations.
	 * 
	 * @param designationCriteria
	 *
	 * @param pageable            the pagination information.
	 * @return the list of entities.
	 */
	Page<DesignationDTO> findAll(DesignationCriteria designationCriteria, Pageable pageable);

	/**
	 * Get the "id" designation.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<DesignationDTO> findOne(Long id);

	/**
	 * Delete the "id" designation.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	ResponseCodeDTO getGeneratedDesignationCode();

	List<DesignationDTO> getByIds(List<Long> designationIds);
}
