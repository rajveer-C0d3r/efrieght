package com.grtship.mdm.serviceimpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.dto.EntityBusinessTypeDTO;
import com.grtship.core.enumeration.EntityType;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.domain.EntityBusinessTypeId;
import com.grtship.mdm.domain.ExternalEntity;
import com.grtship.mdm.mapper.EntityBusinessTypeMapper;
import com.grtship.mdm.repository.EntityBusinessTypeRepository;
import com.grtship.mdm.service.EntityBusinessTypeService;

/**
 * Service Implementation for managing {@link EntityBusinessType}.
 */
@Service
@Transactional
public class EntityBusinessTypeServiceImpl implements EntityBusinessTypeService {

	private final EntityBusinessTypeRepository entityBusinessDetailsRepository;

	private final EntityBusinessTypeMapper entityBusinessDetailsMapper;

	public EntityBusinessTypeServiceImpl(EntityBusinessTypeRepository entityBusinessDetailsRepository,
			EntityBusinessTypeMapper entityBusinessDetailsMapper) {
		this.entityBusinessDetailsRepository = entityBusinessDetailsRepository;
		this.entityBusinessDetailsMapper = entityBusinessDetailsMapper;
	}

	private List<EntityBusinessType> saveAll(Set<EntityBusinessType> entityBusinessType) {
		return entityBusinessDetailsRepository.saveAll(entityBusinessType);
	}

	@Override
	public Set<String> getEntityBusinessTypesByEntityId(Long entityId) {
		List<EntityBusinessTypeDTO> entityBusinessTypeDto = entityBusinessDetailsMapper
				.toDto(entityBusinessDetailsRepository.findByIdExternalEntityId(entityId));
		if (!CollectionUtils.isEmpty(entityBusinessTypeDto)) {
			return entityBusinessTypeDto.stream().filter(obj -> obj.getExternalEntityId() != null)
					.map(obj -> obj.getEntityType().name()).collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	private void deleteByEntityTypeAndExternalEntity(EntityType entityType, Long entityId) {
		entityBusinessDetailsRepository.deleteByIdEntityTypeAndIdExternalEntityId(entityType, entityId);
	}

	@Override
	public void saveAll(Set<String> entityDetails, ExternalEntity savedEntity) {
		if (!CollectionUtils.isEmpty(entityDetails)) {
			Set<EntityBusinessType> entityBusinessType = new HashSet<>();
			entityDetails.forEach(entityType -> {
				EntityBusinessTypeId entityId = new EntityBusinessTypeId();
				for (EntityType value : EntityType.values()) {
					if (value.name().equals(entityType)) {
						entityId.setEntityType(value);
					}
				}
				entityId.setExternalEntity(savedEntity);
				EntityBusinessType entityBusiness = new EntityBusinessType(entityId);
				entityBusinessType.add(entityBusiness);
			});
			saveAll(entityBusinessType);
		}
	}

	/**** this service used to delete data on Update.... ****/
	@Override
	public void deleteEntityBusinessTypeOnUpdate(Set<String> entityDetails, Long entityId) {
		Set<String> savedEntityBusinessTypes = getEntityBusinessTypesByEntityId(entityId);
		if (!CollectionUtils.isEmpty(entityDetails))
			savedEntityBusinessTypes.removeAll(entityDetails);
		if (!CollectionUtils.isEmpty(savedEntityBusinessTypes)) {
			savedEntityBusinessTypes
					.forEach(string -> deleteByEntityTypeAndExternalEntity(EntityType.valueOf(string), entityId));
		}
	}
}
