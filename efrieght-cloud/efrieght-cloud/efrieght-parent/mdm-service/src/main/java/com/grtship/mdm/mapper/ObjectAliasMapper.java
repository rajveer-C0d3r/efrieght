package com.grtship.mdm.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.mdm.domain.ObjectAlias;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link ObjectAlias} and its DTO {@link ObjectAliasDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface ObjectAliasMapper extends EntityMapper<ObjectAliasDTO, ObjectAlias> {

	ObjectAliasDTO toDto(ObjectAlias objectAlias);

	ObjectAlias toEntity(ObjectAliasDTO objectaliasDto);

	default ObjectAlias fromId(Long id) {
		if (id == null) {
			return null;
		}
		ObjectAlias objectAlias = new ObjectAlias();
		objectAlias.setId(id);
		return objectAlias;
	}

	Set<ObjectAlias> toEntity(Set<ObjectAliasDTO> aliasDto);
}
