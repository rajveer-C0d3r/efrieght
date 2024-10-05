package com.grtship.mdm.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.DocumentDownloadDTO;
import com.grtship.mdm.criteria.DocumentCriteria;

/**
 * Query Service Interface for managing
 * {@link com.grt.efreight.domain.Destination}.
 */
public interface DocumentQueryService {

	List<DocumentDTO> findAll(DocumentCriteria documentCriteria);

	Optional<DocumentDTO> findOne(Long id);

	Set<DocumentDownloadDTO> getDocumentsOnEdit(Long referenceId, String referenceName);

}
