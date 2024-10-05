package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.domain.DocumentStorage;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link DocumentStorage} and its DTO
 * {@link DocumentStorageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
@Component
public interface DocumentStorageMapper extends EntityMapper<DocumentStorageDTO, DocumentStorage> {

	default DocumentStorage fromId(Long id) {
		if (id == null) {
			return null;
		}
		DocumentStorage documentStorage = new DocumentStorage();
		documentStorage.setId(id);
		return documentStorage;
	}
}
