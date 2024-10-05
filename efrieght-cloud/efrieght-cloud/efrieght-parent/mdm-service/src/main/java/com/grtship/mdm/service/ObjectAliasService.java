package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.domain.ObjectAlias;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.ObjectAlias}.
 */
public interface ObjectAliasService {

	/**
	 * Save a objectAlias.
	 *
	 * @param objectAliasDTO the entity to save.
	 * @return the persisted entity.
	 */
	ObjectAliasDTO save(ObjectAliasDTO objectAliasDTO);

	/**
	 * Get all the objectAliases.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<ObjectAliasDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" objectAlias.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<ObjectAliasDTO> findOne(Long id);

	/**
	 * Delete the "id" objectAlias.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	List<ObjectAlias> saveAll(Set<ObjectAlias> aliases);

	Set<Long> getAliasIdByReferenceIdAndReferenceName(Long id, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedAliasIdsSetForEntity);

	void checkForDuplicateAlias(String name, String referenceName, Long clientId, Long companyId);

	void checkForDuplicateAlias(Long id, String name, String referenceName, Long clientId, Long companyId);

	void saveAll(Set<ObjectAliasDTO> aliases, Long referenceId, String referenceName, Long clientId, Long companyId,
			Long branchId);

	void deleteAliasOnUpdate(Set<ObjectAliasDTO> externalEntityAlias, String referenceName, Long referenceId);// this
																												// will
																												// perform
																												// hard-delete
	
	public void checkForDuplicateAlias(String name,String referenceName);
	
	public void checkForDuplicateAlias(Long id,String name,String referenceName);

	ValidationError checkForDuplicateAlias(String referenceName, Set<ObjectAliasDTO> objectAlias, Long clientId,
			Long companyId);

	ValidationError checkForDuplicateAlias(Set<String> names, String referenceName, Long clientId, Long companyId);
	
	ValidationError checkForDuplicateAlias(Set<String> names,String referenceName);
	
	ValidationError checkForDuplicateAlias(Set<ObjectAliasDTO> names,String referenceName,Long id);
}
