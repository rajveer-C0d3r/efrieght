package com.grtship.mdm.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.core.dto.DocumentDTO;
import com.grtship.mdm.criteria.DocumentCriteria;
import com.grtship.mdm.service.DocumentQueryService;
import com.grtship.mdm.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Document}.
 */
@Slf4j
@RestController
@RequestMapping("/api/document-definition")
public class DocumentController {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private DocumentQueryService documentQueryService;

	/**
	 * {@code GET  /documents} : get all the documents.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of documents in body.
	 */
	@GetMapping
	public List<DocumentDTO> getAllDocuments(DocumentCriteria documentCriteria) {
		log.debug("REST request to get all Documents");
		return documentQueryService.findAll(documentCriteria);
	}

	/**
	 * {@code GET  /documents/:id} : get the "id" document.
	 *
	 * @param id the id of the documentDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the documentDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
		log.debug("REST request to get Document : {}", id);
		Optional<DocumentDTO> documentDTO = documentQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(documentDTO);
	}
}
