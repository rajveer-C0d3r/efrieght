package com.grtship.authorisation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.authorisation.interfaces.EntityMapper;
import com.grtship.core.dto.ObjectModuleDTO;

/**
 * Mapper for the entity {@link ObjectModule} and its DTO
 * {@link ObjectModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = { ObjectApprovalSequenceMapper.class })
@Component
public interface ObjectModuleMapper extends EntityMapper<ObjectModuleDTO, ObjectModule> {

	@Mapping(target = "objectApprovalSequences", source = "objectApprovalSequence")
	ObjectModule toEntity(ObjectModuleDTO objectModuleDTO);

	@Mapping(target = "objectApprovalSequence", source = "objectApprovalSequences")
	ObjectModuleDTO toDto(ObjectModule entity);

	default ObjectModule fromId(Long id) {
		if (id == null) {
			return null;
		}
		ObjectModule objectModule = new ObjectModule();
		objectModule.setId(id);
		return objectModule;
	}
}
