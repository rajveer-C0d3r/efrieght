package com.grtship.mdm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.interfaces.EntityMapper;

/**
 * Mapper for the entity {@link Document} and its DTO {@link DocumentDTO}.
 */
@Mapper(componentModel = "spring", uses = { CountryMapper.class })
@Component
public interface DocumentMapper extends EntityMapper<DocumentDTO, Document> {

	DocumentDTO toDto(Document document);

	Document toEntity(DocumentDTO documentDTO);

	@Mapping(source = "documentStorageDto.fileStorage", target = "fileName")
	@Mapping(source = "documentStorageDto.originalFileName", target = "originalFileName")
	@Mapping(source = "documentStorageDto.referenceId", target = "referenceId")
	@Mapping(source = "documentStorageDto.referenceName", target = "referenceName")
	@Mapping(source = "document.code", target = "code")
	@Mapping(source = "document.name", target = "name")
	@Mapping(source = "documentStorageDto.id", target = "id")
	public abstract DocumentDownloadDTO toDto(DocumentStorageDTO documentStorageDto, Document document);

	default Document fromId(Long id) {
		if (id == null) {
			return null;
		}
		Document document = new Document();
		document.setId(id);
		return document;
	}
}
