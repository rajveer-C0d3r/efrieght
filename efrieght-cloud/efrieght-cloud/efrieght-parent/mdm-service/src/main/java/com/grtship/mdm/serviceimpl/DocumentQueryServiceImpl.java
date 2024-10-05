package com.grtship.mdm.serviceimpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.core.annotation.AccessFilter;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.core.dto.DocumentStorageDTO;
import com.grtship.mdm.criteria.DocumentCriteria;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.domain.Document_;
import com.grtship.mdm.mapper.DocumentMapper;
import com.grtship.mdm.repository.DocumentRepository;
import com.grtship.mdm.service.DocumentQueryService;
import com.grtship.mdm.service.DocumentStorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DocumentQueryServiceImpl implements DocumentQueryService {

	@Autowired
	private DocumentStorageService documentStorageService;
	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;

	DocumentQueryServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.documentMapper = documentMapper;
	}

	@AccessFilter(clientAccessFlag = true, companyAccessFlag = true, branchAccessFlag = true)
	@Override
	public List<DocumentDTO> findAll(DocumentCriteria documentCriteria) {
		Specification<Document> documentSpecification = createSpecification(documentCriteria);
		List<Document> documents = documentRepository.findAll(documentSpecification);
		return documents.stream().map(documentMapper::toDto).collect(Collectors.toList());
	}

	private Specification<Document> createSpecification(DocumentCriteria documentCriteria) {
		Specification<Document> documentSpecification = Specification.where(null);
		if (documentSpecification != null) {
			if (documentCriteria.getReferenceId() != null) {
				documentSpecification = documentSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder
						.equal(root.get(Document_.referenceId), documentCriteria.getReferenceId()));
			}
			if (documentCriteria.getReferenceName() != null) {
				documentSpecification = documentSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder
						.equal(root.get(Document_.referenceName), documentCriteria.getReferenceName()));
			}
			if (documentCriteria.getDocumentType() != null) {
				documentSpecification = documentSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder
						.equal(root.get(Document_.TYPE), documentCriteria.getDocumentType()));
			}
			if (documentCriteria.getId() != null) {
				documentSpecification = documentSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder
						.equal(root.get(Document_.ID), documentCriteria.getId()));
			}
		}
		return documentSpecification;
	}

	public Long getDocumentIdByCode(String code) {
		return documentRepository.findIdByCode(code);
	}

	public Set<Long> getIdByReferenceIdAndReferenceName(Long referenceId, String referenceName) {
		return documentRepository.findIdsByReferenceIdReferenceName(referenceId, referenceName);
	}

	@Override
	public Optional<DocumentDTO> findOne(Long id) {
		log.debug("Request to get Document : {}", id);
		DocumentCriteria criteria = new DocumentCriteria().id(id);
		List<DocumentDTO> documents = findAll(criteria);
		if (!CollectionUtils.isEmpty(documents)) {
			return Optional.ofNullable(documents.get(0));
		}
		return Optional.empty();
	}

	/**
	 * getDocumentMapForEdit is used to prepare upload documents grid data for
	 * edit..
	 * 
	 **/
	@Override
	public Set<DocumentDownloadDTO> getDocumentsOnEdit(Long referenceId, String referenceName) {
		Set<DocumentDownloadDTO> documents = new HashSet<>();
		if (referenceId == null) {
			return Collections.emptySet();
		}
		List<DocumentStorageDTO> documentStorageList = documentStorageService
				.getByReferenceIdsAndName(Arrays.asList(referenceId), referenceName);
		Map<Long, Document> documentMap = getDocumentMap(documentStorageList);
		if (!CollectionUtils.isEmpty(documentStorageList)) {
			documents = documentStorageList.stream().map(doc -> {
				Document document = documentMap.get(doc.getDocumentDefinitionId());
				return documentMapper.toDto(doc, document);
			}).collect(Collectors.toSet());
		}
		return documents;
	}

	private Map<Long, Document> getDocumentMap(List<DocumentStorageDTO> documentStorageList) {
		List<Long> documentIds = documentStorageList.stream().filter(obj -> obj.getDocumentDefinitionId() != null)
				.map(DocumentStorageDTO::getDocumentDefinitionId).collect(Collectors.toList());
		List<Document> documentList = documentRepository.findByIdIn(documentIds);
		return documentList.stream().filter(obj -> obj.getId() != null)
				.collect(Collectors.toMap(Document::getId, obj -> obj));
	}
}
