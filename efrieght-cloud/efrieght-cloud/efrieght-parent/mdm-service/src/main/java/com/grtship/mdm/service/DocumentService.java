package com.grtship.mdm.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.mdm.domain.Document;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Document}.
 */
public interface DocumentService {

	/**
	 * Save a document.
	 *
	 * @param documentDTO the entity to save.
	 * @return the persisted entity.
	 */
	DocumentDTO save(DocumentDTO documentDTO);

	/**
	 * Delete the "id" document.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	List<Document> saveAll(Collection<Document> documents);

	Map<Long, Set<DocumentDTO>> getMapOfDocumentsByReferenceIdListAndReferenceName(List<Long> enittyIdList,
			String referenceName);

	void deleteByReferenceNameAndIdIn(String referenceName, Set<Long> documentIds);

}
