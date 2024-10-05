package com.grtship.account.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.account.domain.ObjectAlias;
import com.grtship.account.mapper.ObjectAliasMapper;
import com.grtship.account.repository.ObjectAliasRepository;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.ValidationErrorType;

/**
 * Service Implementation for managing {@link ObjectAlias}.
 */
@Service
@Transactional
public class ObjectAliasServiceImpl implements ObjectAliasService {

	private static final String ALREADY_EXISTS_ENTER_ANOTHER_ALIAS = " Already Exists, Enter another alias.";

	private static final String ALIAS = "Alias ";

	private final Logger log = LoggerFactory.getLogger(ObjectAliasServiceImpl.class);

	private final ObjectAliasRepository objectAliasRepository;

	private final ObjectAliasMapper objectAliasMapper;

	public ObjectAliasServiceImpl(ObjectAliasRepository objectAliasRepository, ObjectAliasMapper objectAliasMapper) {
		this.objectAliasRepository = objectAliasRepository;
		this.objectAliasMapper = objectAliasMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE,module = com.grtship.core.annotation.Auditable.Module.OBJECT_ALIAS)
	public ObjectAliasDTO save(ObjectAliasDTO objectAliasDTO) {
		log.debug("Request to save ObjectAlias : {}", objectAliasDTO);
		ObjectAlias objectAlias = objectAliasMapper.toEntity(objectAliasDTO);
		objectAlias = objectAliasRepository.save(objectAlias);
		return objectAliasMapper.toDto(objectAlias);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ObjectAliasDTO> findAll(Pageable pageable) {
		log.debug("Request to get all ObjectAliases");
		return objectAliasRepository.findAll(pageable).map(objectAliasMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectAliasDTO> findOne(Long id) {
		log.debug("Request to get ObjectAlias : {}", id);
		return objectAliasRepository.findById(id).map(objectAliasMapper::toDto);
	}

	@Override
	@Auditable(action = ActionType.DELETE,module = com.grtship.core.annotation.Auditable.Module.OBJECT_ALIAS)
	public void delete(Long id) {
		log.debug("Request to delete ObjectAlias : {}", id);
		objectAliasRepository.deleteById(id);
	}

	@Override
	public List<ObjectAlias> saveAll(Collection<ObjectAlias> aliases) {
		log.debug("Request to save list of ObjectAlias : {}", aliases);
		return objectAliasRepository.saveAll(aliases);
	}

	@Override
	public Map<Long, Set<ObjectAliasDTO>> getListOfAliasByReferanceIdListAndReferenceName(
			Collection<Long> referenceIdList, String referenceName) {
		if (!CollectionUtils.isEmpty(referenceIdList)) {
			List<ObjectAlias> aliasList = objectAliasRepository.findByReferenceNameAndReferenceIdIn(referenceName,
					referenceIdList);
			Set<ObjectAliasDTO> aliasDtoList = new HashSet<>();
			if (!CollectionUtils.isEmpty(aliasList)) {
				for (ObjectAlias objectAlias : aliasList) {
					ObjectAliasDTO alias = objectAliasMapper.toDto(objectAlias);
					aliasDtoList.add(alias);
				}
				return aliasDtoList.stream()
						.filter(obj -> obj.getReferenceId() != null && obj.getReferenceName() != null)
						.collect(Collectors.groupingBy(ObjectAliasDTO::getReferenceId, Collectors.toSet()));
			}
			return Collections.emptyMap();
		}
		return Collections.emptyMap();
	}

	@Override
	public Set<Long> getAliasIdByReferenceIdAndReferenceName(Long referenceId, String referenceName) {
		log.debug("Request to get alias ids by reference id and reference name : {} {}", referenceId, referenceName);
		return objectAliasRepository.findByReferenceIdAndReferenceName(referenceId, referenceName).stream()
				.filter(obj -> obj.getId() != null).map(ObjectAlias::getId).collect(Collectors.toSet());
	}

	@Override
	public void deleteByReferenceNameAndIdIn(String referenceName, Collection<Long> aliasIdsToDelete) {
		log.debug("Request to delete alias by reference name and aliasIds : {} {}", referenceName, aliasIdsToDelete);
		if (!CollectionUtils.isEmpty(aliasIdsToDelete))
			objectAliasRepository.deleteByReferenceNameAndIdIn(referenceName, aliasIdsToDelete);
	}

	@Override
	public void checkForDuplicateAlias(String name, String referenceName, Long clientId, Long companyId) {
		if (objectAliasRepository.findByNameAndReferenceNameAndClientIdAndCompanyId(name, referenceName, clientId,
				companyId) != null) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					ALIAS + name + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS);
		}
	}

	@Override
	public void checkForDuplicateAlias(Long id, String name, String referenceName, Long clientId, Long companyId) {
		if (objectAliasRepository.findByIdNotAndNameAndReferenceNameAndClientIdAndCompanyId(id, name, referenceName,
				clientId, companyId) != null) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					ALIAS + name + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS);
		}
	}

	/***
	 * this is service to save all alias with referenceId and referenceName...
	 ***/
	@Override
	public void saveAll(Set<ObjectAliasDTO> aliases, Long referenceId, String referenceName) {
		if (!CollectionUtils.isEmpty(aliases)) {
			Set<ObjectAlias> objectAliases = objectAliasMapper.toEntity(aliases);
			objectAliases.forEach(alias -> {
				alias.setReferenceId(referenceId);
				alias.setReferenceName(referenceName);
			});
			objectAliasRepository.saveAll(objectAliases);
		}
	}

	/*** this service used to do hard-delete on update... ***/
	@Override
	public void deleteAliasOnUpdate(Set<ObjectAliasDTO> objectAlias, String referenceName, Long referenceId) {
		Set<Long> savedAliasIdsSetForEntity = getAliasIdByReferenceIdAndReferenceName(referenceId, referenceName);
		if (!CollectionUtils.isEmpty(objectAlias)) {
			Set<Long> aliasIdsToSaveOrUpdate = objectAlias.stream().filter(obj -> obj.getId() != null)
					.map(ObjectAliasDTO::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(aliasIdsToSaveOrUpdate))
				savedAliasIdsSetForEntity.removeAll(aliasIdsToSaveOrUpdate);
		}
		if (!CollectionUtils.isEmpty(savedAliasIdsSetForEntity)) {
			deleteByReferenceNameAndIdIn(ReferenceNameConstant.GROUP, savedAliasIdsSetForEntity);
		}
	}

	@Override
	public ValidationError checkForDuplicateAlias(Set<String> names, String referenceName, Long clientId,
			Long companyId) {
		Set<String> duplicateAlias = new HashSet<String>();
		names.stream().forEach(name -> {
			if (objectAliasRepository.findByNameAndReferenceNameAndClientIdAndCompanyId(name, referenceName, clientId,
					companyId) != null) {
				duplicateAlias.add(name);
			}
			;
		});
		if (!CollectionUtils.isEmpty(duplicateAlias)) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(ALIAS + duplicateAlias.stream().collect(Collectors.joining(","))
							+ ALREADY_EXISTS_ENTER_ANOTHER_ALIAS)
					.build();
		}
		return null;

	}

	@Override
	public ValidationError checkForDuplicateEntityAlias(Set<ObjectAliasDTO> objectAlias, String referenceName,
			Long clientId, Long companyId) {
		Set<String> duplicateAlias= new HashSet<String>();
		objectAlias.stream().filter(alias -> alias.getId() != null).forEach(alias -> { 
			   if(objectAliasRepository.findByIdNotAndNameAndReferenceNameAndClientIdAndCompanyId(alias.getId(),alias.getName(), referenceName,
						clientId, companyId)!=null) {
				   duplicateAlias.add(alias.getName());
			   };	
		});
		
		objectAlias.stream().filter(alias -> alias.getId() == null).forEach(alias -> {
		          if(objectAliasRepository.findByNameAndReferenceNameAndClientIdAndCompanyId(alias.getName(),
				referenceName, clientId, companyId)!=null) {
		        	  duplicateAlias.add(alias.getName());
		          }
		});
		if (!CollectionUtils.isEmpty(duplicateAlias)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,ALIAS + duplicateAlias.stream().collect(Collectors.joining(",")) + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS);
		}
		return null;
	}
}
