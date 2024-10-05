package com.grtship.mdm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.EntityBusinessTypeDTO;
import com.grtship.mdm.domain.EntityBusinessType;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link EntityBusinessType} and its DTO {@link EntityBusinessTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface EntityBusinessTypeMapper extends EntityMapper<EntityBusinessTypeDTO, EntityBusinessType> {

	@Mapping(source = "id.externalEntity.id", target = "externalEntityId")
	@Mapping(source = "id.entityType", target = "entityType")
	EntityBusinessTypeDTO toDto(EntityBusinessType objectAlias);

	@Mapping(source = "externalEntityId", target = "id.externalEntity.id")
	@Mapping(source = "entityType", target = "id.entityType")
	EntityBusinessType toEntity(EntityBusinessTypeDTO objectaliasDto);


    default EntityBusinessType fromId(Long id) {
        if (id == null) {
            return null;
        }
        EntityBusinessType entityBusinessDetails = new EntityBusinessType();
        return entityBusinessDetails;
    }
}
