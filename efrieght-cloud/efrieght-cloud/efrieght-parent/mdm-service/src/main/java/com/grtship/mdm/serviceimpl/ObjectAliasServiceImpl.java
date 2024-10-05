package com.grtship.mdm.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.constant.ReferenceNameConstant;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.mapper.ObjectAliasMapper;
import com.grtship.mdm.repository.ObjectAliasRepository;
import com.grtship.mdm.service.ObjectAliasService;

/**
 * Service Implementation for managing {@link ObjectAlias}.
 */
@Service
@Transactional
public class ObjectAliasServiceImpl implements ObjectAliasService {

	private static final String ALREADY_EXISTS_ENTER_ANOTHER_ALIAS = " Already Exists, Enter Another Alias";

	private static final String YOUR_SELECTED_ALIAS = "Your selected alias '";

	private static final String HAS_ALREADY_PRESENT_FOR_ANOTHER_ENTITY_SO_PLEASE_SELECT_ANOTHER_NAME_FOR_ALIAS = "' has already present for another Entity. So please select another name for alias";

	private static final String ALIAS = "Alias ";

	private final Logger log = LoggerFactory.getLogger(ObjectAliasServiceImpl.class);

	private final ObjectAliasRepository objectAliasRepository;

	private final ObjectAliasMapper objectAliasMapper;

	public ObjectAliasServiceImpl(ObjectAliasRepository objectAliasRepository, ObjectAliasMapper objectAliasMapper) {
		this.objectAliasRepository = objectAliasRepository;
		this.objectAliasMapper = objectAliasMapper;
	}

	@Override
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.OBJECT_ALIAS)
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
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.OBJECT_ALIAS)
	public void delete(Long id) {
		log.debug("Request to delete ObjectAlias : {}", id);
		objectAliasRepository.deleteById(id);
	}

	@Override
	public List<ObjectAlias> saveAll(Set<ObjectAlias> aliases) {
		List<ObjectAlias> aliasList = new ArrayList<>(aliases);
		if (!CollectionUtils.isEmpty(aliases)) {
			String referenceName = aliasList.get(0).getReferenceName();
			validateAlias(aliasList, referenceName);
			return objectAliasRepository.saveAll(aliases);
		}
		return Collections.emptyList();
	}

	private void validateAlias(List<ObjectAlias> aliases, String referenceName) {
		Set<Long> aliasIdList = aliases.stream().filter(obj -> obj.getId() != null).map(ObjectAlias::getId)
				.collect(Collectors.toSet());
		List<String> aliasName = aliases.stream().filter(a -> a.getName() != null).map(ObjectAlias::getName)
				.collect(Collectors.toList());
		List<String> savedAliasNames = getAliasBasedOnNamesClientIdAnComId(aliasName, referenceName, aliasIdList);
		if (!CollectionUtils.isEmpty(savedAliasNames)) {
			String duplicateAlias = savedAliasNames.stream().map(Object::toString).collect(Collectors.joining(","));
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, YOUR_SELECTED_ALIAS + duplicateAlias
					+ HAS_ALREADY_PRESENT_FOR_ANOTHER_ENTITY_SO_PLEASE_SELECT_ANOTHER_NAME_FOR_ALIAS);
		}
	}

	@Override
	public Set<Long> getAliasIdByReferenceIdAndReferenceName(Long referenceId, String referenceName) {
		return objectAliasRepository.findIdsByReferenceIdAndReferenceName(referenceId, referenceName);
	}

	@Override
	public void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> savedAliasIdsSetForReferenceId) {
		if (!CollectionUtils.isEmpty(savedAliasIdsSetForReferenceId))
			objectAliasRepository.deleteByReferenceNameAndIdIn(referenceName, savedAliasIdsSetForReferenceId);
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

	private List<String> getAliasBasedOnNamesClientIdAnComId(List<String> aliasNames, String referenceName,
			Set<Long> aliasIdList) {
		List<String> alias = objectAliasRepository.getAliasNamesBasedOnNamesClientIdAnComId(aliasNames, referenceName,
				aliasIdList);
		return (!CollectionUtils.isEmpty(alias)) ? alias : Collections.emptyList();
	}

	/*** this service has used to save alias..... ***/
	@Override
	public void saveAll(Set<ObjectAliasDTO> aliases, Long referenceId, String referenceName, Long clientId,
			Long companyId, Long branchId) {
		if (!CollectionUtils.isEmpty(aliases)) {
			Set<ObjectAlias> objectAliases = objectAliasMapper.toEntity(aliases);
			objectAliases.forEach(alias -> {
				alias.setReferenceId(referenceId);
				alias.setReferenceName(referenceName);
				alias.setClientId(clientId);
				alias.setCompanyId(companyId);
			});
			validateAlias(new ArrayList<>(objectAliases), referenceName);
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
			deleteByReferenceNameAndIdIn(ReferenceNameConstant.ENTITY, savedAliasIdsSetForEntity);
		}
	}
	

	@Override
	public void checkForDuplicateAlias(String name, String referenceName) {
		if (objectAliasRepository.findByNameAndReferenceName(name, referenceName)!= null) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,ALIAS + name + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS);
		}
	}

	@Override
	public void checkForDuplicateAlias(Long id, String name, String referenceName) {
		if (objectAliasRepository.findByIdNotAndNameAndReferenceName(id,name, referenceName)!= null) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,ALIAS + name + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS);
		}
	}
	
	@Override
	public ValidationError checkForDuplicateAlias(Set<String> names, String referenceName, Long clientId, Long companyId) {
		Set<String> duplicateAlias= new HashSet<String>();
		names.stream().forEach(alias -> { 
			   if(objectAliasRepository.findByNameAndReferenceNameAndClientIdAndCompanyId(alias,
					referenceName, clientId, companyId)!=null) {
				   duplicateAlias.add(alias);
			   };	
		});
		if (!CollectionUtils.isEmpty(duplicateAlias)) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(ALIAS + duplicateAlias.stream().collect(Collectors.joining(",")) + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS)
					.build();
		}
		return null;
	}
	
	@Override
	public ValidationError checkForDuplicateAlias(String referenceName, Set<ObjectAliasDTO> objectAlias,Long clientId, Long companyId) {
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
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(ALIAS + duplicateAlias.stream().collect(Collectors.joining(",")) + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS)
					.build();
		}
		return null;
	}

	@Override
	public ValidationError checkForDuplicateAlias(Set<String> names, String referenceName) {
		Set<String> duplicateAlias= new HashSet<String>();
		names.stream().forEach(aliasName -> { 
			if (objectAliasRepository.findByNameAndReferenceName(aliasName, referenceName)!= null) {
				duplicateAlias.add(aliasName);
			}
		});
		return returnAliasValidationError(duplicateAlias);
	}

	private ValidationError returnAliasValidationError(Set<String> duplicateAlias) {
		if (!CollectionUtils.isEmpty(duplicateAlias)) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(ALIAS + duplicateAlias.stream().collect(Collectors.joining(",")) + ALREADY_EXISTS_ENTER_ANOTHER_ALIAS)
					.build();
		}
		return null;
	}

	@Override
	public ValidationError checkForDuplicateAlias(Set<ObjectAliasDTO> objectAlias, String referenceName, Long id) {
		Set<String> duplicateAlias= new HashSet<String>();
		objectAlias.stream().filter(alias -> alias.getId() != null).map(ObjectAliasDTO::getName).forEach(aliasName -> { 
			   if(objectAliasRepository.findByIdNotAndNameAndReferenceName(id,aliasName, referenceName)!= null) {
				   duplicateAlias.add(aliasName);
			   };	
		});
		
		objectAlias.stream().filter(alias -> alias.getId() == null).map(ObjectAliasDTO::getName).forEach(aliasName -> {
			if (objectAliasRepository.findByNameAndReferenceName(aliasName, referenceName)!= null) {
				duplicateAlias.add(aliasName);
			}
		});
		return returnAliasValidationError(duplicateAlias);
	}


}
