package com.grtship.client.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.grtship.client.domain.Document;
import com.grtship.core.dto.DocumentDTO;

/**
 * Mapper for the entity {@link Document} and its DTO {@link DocumentDTO}.
 */
@Mapper(componentModel = "spring", uses = {ClientMapper.class})
@Component
public interface DocumentMapper extends EntityMapper<DocumentDTO, Document> {

    @Mapping(source = "documentCode", target = "code")
	@Mapping(source = "documentName", target = "name")
    DocumentDTO toDto(Document document);

    @Mapping(source = "code", target = "documentCode")
	@Mapping(source = "name", target = "documentName")
    Document toEntity(DocumentDTO documentDTO);

    default Document fromId(Long id) {
        if (id == null) {
            return null;
        }
        Document document = new Document();
        document.setId(id);
        return document;
    }
}
