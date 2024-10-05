package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.ObjectCodeDTO;
import com.grtship.mdm.criteria.ObjectCodeCriteria;
import com.grtship.mdm.service.ObjectCodeService;
import com.grtship.mdm.serviceimpl.ObjectCodeFilterServiceImpl;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.ObjectCode}.
 */
@RestController
@RequestMapping("/api/object-codes")
public class ObjectCodeController {

	private final Logger log = LoggerFactory.getLogger(ObjectCodeController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceObjectCode";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ObjectCodeService objectCodeService;

	private final ObjectCodeFilterServiceImpl objectCodeQueryService;

	public ObjectCodeController(ObjectCodeService objectCodeService, ObjectCodeFilterServiceImpl objectCodeQueryService) {
		this.objectCodeService = objectCodeService;
		this.objectCodeQueryService = objectCodeQueryService;
	}

	/**
	 * {@code POST  /object-codes} : Create a new objectCode.
	 *
	 * @param objectCodeDTO the objectCodeDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new objectCodeDTO, or with status {@code 400 (Bad Request)}
	 *         if the objectCode has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<ObjectCodeDTO> createObjectCode(@Valid @RequestBody ObjectCodeDTO objectCodeDTO)
			throws URISyntaxException {
		log.debug("REST request to save ObjectCode : {}", objectCodeDTO);
		if (objectCodeDTO.getId() != null) {
			throw new BadRequestAlertException("A new objectCode cannot already have an ID", ENTITY_NAME, "idexists");
		}
		ObjectCodeDTO result = objectCodeService.save(objectCodeDTO);
		return ResponseEntity
				.created(new URI("/api/object-codes/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /object-codes} : Updates an existing objectCode.
	 *
	 * @param objectCodeDTO the objectCodeDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated objectCodeDTO, or with status {@code 400 (Bad Request)}
	 *         if the objectCodeDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the objectCodeDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<ObjectCodeDTO> updateObjectCode(@Valid @RequestBody ObjectCodeDTO objectCodeDTO)
			throws URISyntaxException {
		log.debug("REST request to update ObjectCode : {}", objectCodeDTO);
		if (objectCodeDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ObjectCodeDTO result = objectCodeService.save(objectCodeDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				objectCodeDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /object-codes} : get all the objectCodes.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of objectCodes in body.
	 */
	@GetMapping
	public ResponseEntity<List<ObjectCodeDTO>> getAllObjectCodes(ObjectCodeCriteria criteria, Pageable pageable) {
		log.debug("REST request to get ObjectCodes by criteria: {}", criteria);
		Page<ObjectCodeDTO> page = objectCodeQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /object-codes/count} : count all the objectCodes.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("/count")
	public ResponseEntity<Long> countObjectCodes(ObjectCodeCriteria criteria) {
		log.debug("REST request to count ObjectCodes by criteria: {}", criteria);
		return ResponseEntity.ok().body(objectCodeQueryService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /object-codes/:id} : get the "id" objectCode.
	 *
	 * @param id the id of the objectCodeDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the objectCodeDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<ObjectCodeDTO> getObjectCode(@PathVariable Long id) {
		log.debug("REST request to get ObjectCode : {}", id);
		Optional<ObjectCodeDTO> objectCodeDTO = objectCodeService.findOne(id);
		return ResponseUtil.wrapOrNotFound(objectCodeDTO);
	}

	/**
	 * {@code DELETE  /object-codes/:id} : delete the "id" objectCode.
	 *
	 * @param id the id of the objectCodeDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteObjectCode(@PathVariable Long id) {
		log.debug("REST request to delete ObjectCode : {}", id);

		objectCodeService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@GetMapping("/generateCode")
	public String generateCode(@RequestParam String objectName, @RequestParam(required = false) String parentCode) {
		log.debug("REST request to generate code : {} {}", objectName, parentCode);
		return objectCodeService.generateCode(objectName, parentCode);
	}
}
