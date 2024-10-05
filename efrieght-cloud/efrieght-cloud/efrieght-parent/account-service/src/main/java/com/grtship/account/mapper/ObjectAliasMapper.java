package com.grtship.account.mapper;

import java.util.Set;

import org.mapstruct.Mapper;

import com.grtship.account.domain.ObjectAlias;
import com.grtship.account.interfaces.EntityMapper;
import com.grtship.core.dto.ObjectAliasDTO;

/**
 * Mapper for the entity {@link ObjectAlias} and its DTO {@link ObjectAliasDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ObjectAliasMapper extends EntityMapper<ObjectAliasDTO, ObjectAlias> {

	Set<ObjectAlias> toEntity(Set<ObjectAliasDTO> alias);

	default ObjectAlias fromId(Long id) {
		if (id == null) {
			return null;
		}
		ObjectAlias objectAlias = new ObjectAlias();
		objectAlias.setId(id);
		return objectAlias;
	}

}
