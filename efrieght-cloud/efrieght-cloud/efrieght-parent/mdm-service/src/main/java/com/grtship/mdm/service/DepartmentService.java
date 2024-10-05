package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.DepartmentDTO;
import com.grtship.mdm.criteria.DepartmentCriteria;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Department}.
 */
public interface DepartmentService {

	/**
	 * Save a department.
	 *
	 * @param departmentDTO the entity to save.
	 * @return the persisted entity.
	 */
	DepartmentDTO save(DepartmentDTO departmentDTO);

	/**
	 * Get all the departments.
	 * 
	 * @param departmentCriteria
	 *
	 * @param pageable           the pagination information.
	 * @return the list of entities.
	 */
	Page<DepartmentDTO> findAll(DepartmentCriteria departmentCriteria, Pageable pageable);

	/**
	 * Get the "id" department.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<DepartmentDTO> findOne(Long id);

	/**
	 * Delete the "id" department.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	List<DepartmentDTO> getDepartmentsByIds(List<Long> departmentIds);

}
