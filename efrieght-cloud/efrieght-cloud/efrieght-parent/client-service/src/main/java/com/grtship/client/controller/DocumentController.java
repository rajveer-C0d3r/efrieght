package com.grtship.client.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grtship.client.service.DocumentService;
import com.grtship.client.util.HeaderUtil;
import com.grtship.client.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.DocumentDTO;

/**
 * REST controller for managing {@link com.grtship.efreight.client.domain.Document}.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private static final String ENTITY_NAME = "clientServiceDocument";
    
    @Value("${spring.application.name}")
	private String applicationName;

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * {@code POST  /documents} : Create a new document.
     *
     * @param documentDTO the documentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new documentDTO, or with status {@code 400 (Bad Request)} if the document has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to save Document : {}", documentDTO);
        if (documentDTO.getId() != null) {
            throw new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.created(new URI("/api/documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /documents} : Updates an existing document.
     *
     * @param documentDTO the documentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated documentDTO,
     * or with status {@code 400 (Bad Request)} if the documentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the documentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping
    public ResponseEntity<DocumentDTO> updateDocument(@RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to update Document : {}", documentDTO);
        if (documentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, documentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /documents} : get all the documents.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of documents in body.
     */
    @GetMapping
    public List<DocumentDTO> getAllDocuments() {
        log.debug("REST request to get all Documents");
        return documentService.findAll();
    }

    /**
     * {@code GET  /documents/:id} : get the "id" document.
     *
     * @param id the id of the documentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the documentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        log.debug("REST request to get Document : {}", id);
        Optional<DocumentDTO> documentDTO = documentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(documentDTO);
    }

    /**
     * {@code DELETE  /documents/:id} : delete the "id" document.
     *
     * @param id the id of the documentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Auditable
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
