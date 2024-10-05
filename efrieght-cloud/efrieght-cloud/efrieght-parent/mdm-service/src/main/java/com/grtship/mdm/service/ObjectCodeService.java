package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.ObjectCodeDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.ObjectCode}.
 */
public interface ObjectCodeService {

	/**
	 * Save a objectCode.
	 *
	 * @param objectCodeDTO the entity to save.
	 * @return the persisted entity.
	 */
	ObjectCodeDTO save(ObjectCodeDTO objectCodeDTO);

	/**
	 * Get all the objectCodes.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<ObjectCodeDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" objectCode.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ObjectCodeDTO> findOne(Long id);

	/**
	 * Delete the "id" objectCode.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	/**
	 * get the objectCode
	 * 
	 * @param1 objectName the objectName of entity
	 * @param2 parentCode the parentCode of entity
	 * 
	 * @return the entity
	 */
	ObjectCodeDTO findByObjectNameAndParentCode(String objectName, String parentCode);

	List<ObjectCodeDTO> findByObjectName(String objectName);

	String generateCode(String objectName, String parentCode);
}
