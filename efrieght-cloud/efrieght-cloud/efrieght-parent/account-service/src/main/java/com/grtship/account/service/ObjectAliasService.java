package com.grtship.account.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.domain.ObjectAlias;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.ObjectAliasDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.ObjectAlias}.
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

	List<ObjectAlias> saveAll(Collection<ObjectAlias> aliases);

	Map<Long, Set<ObjectAliasDTO>> getListOfAliasByReferanceIdListAndReferenceName(Collection<Long> referenceIdList,
			String referenceName);

	Set<Long> getAliasIdByReferenceIdAndReferenceName(Long referenceId, String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Collection<Long> aliasIdsToDelete);

	void checkForDuplicateAlias(String name, String referenceName, Long clientId, Long companyId);

	void checkForDuplicateAlias(Long id, String name, String referenceName, Long clientId, Long companyId);

	void saveAll(Set<ObjectAliasDTO> objectAlias, Long referenceId, String referenceName);

	void deleteAliasOnUpdate(Set<ObjectAliasDTO> aliases, String referenceName, Long referenceId);
	
	ValidationError checkForDuplicateAlias(Set<String> names, String referenceName, Long clientId, Long companyId);
	
	ValidationError checkForDuplicateEntityAlias(Set<ObjectAliasDTO> externalEntityAlias,String referenceName, Long clientId, Long companyId);
}
