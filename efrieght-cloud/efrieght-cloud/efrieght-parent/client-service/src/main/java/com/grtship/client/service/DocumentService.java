package com.grtship.client.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.client.domain.Document;
import com.grtship.client.mapper.DocumentMapper;
import com.grtship.client.repository.DocumentRepository;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Auditable.ActionType;
import com.grtship.core.dto.DocumentDTO;

/**
 * Service Implementation for managing {@link Document}.
 */
@Service
@Transactional
public class DocumentService {

	private final Logger log = LoggerFactory.getLogger(DocumentService.class);

	private final DocumentRepository documentRepository;

	private final DocumentMapper documentMapper;

	public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
	}

	/**
	 * Save a document.
	 *
	 * @param documentDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Auditable(action = ActionType.SAVE, module = com.grtship.core.annotation.Auditable.Module.DOCUMENT)
	public DocumentDTO save(DocumentDTO documentDTO) {
		log.debug("Request to save Document : {}", documentDTO);
		Document document = documentMapper.toEntity(documentDTO);
		document = documentRepository.save(document);
		return documentMapper.toDto(document);
	}

	/**
	 * Get all the documents.
	 *
	 * @return the list of entities.
	 */
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAll() {
		log.debug("Request to get all Documents");
		return documentRepository.findAll().stream().map(documentMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one document by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Transactional(readOnly = true)
	public Optional<DocumentDTO> findOne(Long id) {
		log.debug("Request to get Document : {}", id);
		return documentRepository.findById(id).map(documentMapper::toDto);
	}

	/**
	 * Delete the document by id.
	 *
	 * @param id the id of the entity.
	 */
	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.DOCUMENT)
	public void delete(Long id) {
		log.debug("Request to delete Document : {}", id);
		documentRepository.deleteById(id);
	}

	public List<Document> saveAll(Set<Document> documents) {
		log.debug("Request to save all Documents : {}", documents);
		return documentRepository.saveAll(documents);
	}

	public Map<Long, List<DocumentDTO>> getMapOfDocumentsByReferenceIdList(List<Long> idList, String referenceName) {
		log.debug("Request to get MapOfDocumentsByReferenceIdList : {}", idList);
		List<DocumentDTO> documentList = documentRepository.findByReferenceIdInAndReferenceName(idList, referenceName)
				.stream().map(documentMapper::toDto).collect(Collectors.toList());
		Map<Long, List<DocumentDTO>> documentMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(documentList)) {
			documentMap = documentList.stream().filter(documentDTO -> documentDTO.getReferenceId() != null)
					.collect(Collectors.groupingBy(DocumentDTO::getReferenceId, Collectors.toList()));
		}
		return documentMap;
	}

	public Set<Long> getDocumentIdByReferenceIdAndReferenceName(Long referenceId, String referenceName) {
		return documentRepository.findIdsByReferenceIdReferenceName(referenceId, referenceName);
	}

	@Auditable(action = ActionType.DELETE, module = com.grtship.core.annotation.Auditable.Module.DOCUMENT)
	public void deleteByDocumentIdList(Set<Long> documentIdList) {
		documentRepository.deleteByIdIn(documentIdList);
	}
}
