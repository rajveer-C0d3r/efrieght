
package com.grtship.mdm.serviceimpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.Validate;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.mapper.DocumentMapper;
import com.grtship.mdm.repository.DocumentRepository;
import com.grtship.mdm.service.DocumentService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link Document}.
 */
@Slf4j
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;

	public DocumentServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
	}

	@Override
	@Validate(validator = "documentValidator",action = "save")
	public DocumentDTO save(DocumentDTO documentDTO) {
		log.debug("Request to save Document : {}", documentDTO);
		Document document = documentMapper.toEntity(documentDTO);
		document = documentRepository.save(document);
		return documentMapper.toDto(document);
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete Document : {}", id);
		documentRepository.deleteById(id);
	}

	@Override
	public List<Document> saveAll(Collection<Document> documents) {
		log.debug("Request to save all Documents : {}", documents);
		return documentRepository.saveAll(documents);
	}

	@Override
	public Map<Long, Set<DocumentDTO>> getMapOfDocumentsByReferenceIdListAndReferenceName(List<Long> enittyIdList,
			String referenceName) {
		if (!CollectionUtils.isEmpty(enittyIdList)) {
			List<Document> documenttypeList = documentRepository.findByReferenceIdIn(enittyIdList);
			List<DocumentDTO> documentDtoSet = documentMapper.toDto(documenttypeList);
			if (!CollectionUtils.isEmpty(documentDtoSet)) {
				return documentDtoSet.stream()
						.filter(obj -> obj.getReferenceId() != null && obj.getReferenceName() != null
								&& obj.getReferenceName().equals(referenceName))
						.collect(Collectors.groupingBy(DocumentDTO::getReferenceId, Collectors.toSet()));
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> documentIds) {
		documentRepository.deleteByReferenceNameAndIdIn(referenceName, documentIds);
	}

}
