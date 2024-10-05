package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.mdm.domain.ObjectCode;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link ObjectCode} and its DTO {@link ObjectCodeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface ObjectCodeMapper extends EntityMapper<ObjectCodeDTO, ObjectCode> {

	default ObjectCode fromId(Long id) {
		if (id == null) {
			return null;
		}
		ObjectCode objectCode = new ObjectCode();
		objectCode.setId(id);
		return objectCode;
	}
}
