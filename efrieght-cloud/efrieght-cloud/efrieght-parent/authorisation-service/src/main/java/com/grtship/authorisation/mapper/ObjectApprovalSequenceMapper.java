package com.grtship.authorisation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.domain.ObjectApprovalSequence;
import com.grtship.authorisation.interfaces.EntityMapper;
import com.grtship.core.dto.ObjectApprovalSequenceDTO;

/**
 * Mapper for the entity {@link ObjectApprovalSequence} and its DTO
 * {@link ObjectApprovalSequenceDTO}.
 */
@Mapper(componentModel = "spring", uses = { ObjectModuleMapper.class })
@Component
public interface ObjectApprovalSequenceMapper extends EntityMapper<ObjectApprovalSequenceDTO, ObjectApprovalSequence> {

	ObjectApprovalSequenceDTO toDto(ObjectApprovalSequence objectApprovalSequence);

	ObjectApprovalSequence toEntity(ObjectApprovalSequenceDTO objectApprovalSequenceDTO);

	default ObjectApprovalSequence fromId(Long id) {
		if (id == null) {
			return null;
		}
		ObjectApprovalSequence objectApprovalSequence = new ObjectApprovalSequence();
		objectApprovalSequence.setId(id);
		return objectApprovalSequence;
	}
}
