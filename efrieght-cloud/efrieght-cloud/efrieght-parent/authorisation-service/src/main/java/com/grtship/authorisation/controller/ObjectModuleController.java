package com.grtship.authorisation.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.authorisation.criteria.ObjectModuleCriteria;
import com.grtship.authorisation.service.ObjectModuleQueryService;
import com.grtship.authorisation.service.ObjectModuleService;
import com.grtship.authorisation.util.HeaderUtil;
import com.grtship.authorisation.util.PaginationUtil;
import com.grtship.authorisation.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.AuthorizationContainerDTO;
import com.grtship.core.dto.ObjectModuleDTO;
import com.grtship.core.dto.PageableEntityDTO;

/**
 * REST controller for managing {@link com.grt.efreight.domain.ObjectModule}.
 */
@RestController
@RequestMapping("/api/object-module")
public class ObjectModuleController {

	private final Logger log = LoggerFactory.getLogger(ObjectModuleController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceObjectModule";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ObjectModuleService objectModuleService;

	private final ObjectModuleQueryService objectModuleQueryService;

	public ObjectModuleController(ObjectModuleService objectModuleService,
			ObjectModuleQueryService objectModuleQueryService) {
		this.objectModuleService = objectModuleService;
		this.objectModuleQueryService = objectModuleQueryService;
	}

	/**
	 * {@code POST  /object-modules} : Create a new objectModule.
	 *
	 * @param objectModuleDTO the objectModuleDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new objectModuleDTO, or with status
	 *         {@code 400 (Bad Request)} if the objectModule has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_OBJECT_MODULE_ADD + "\")")
	public ResponseEntity<ObjectModuleDTO> createObjectModule(@Valid @RequestBody ObjectModuleDTO objectModuleDTO)
			throws URISyntaxException {
		log.debug("REST request to save ObjectModule : {}", objectModuleDTO);
		if (objectModuleDTO.getId() != null) {
			throw new BadRequestAlertException("A new objectModule cannot already have an ID", ENTITY_NAME, "idexists");
		}
		ObjectModuleDTO result = objectModuleService.save(objectModuleDTO);
		return ResponseEntity
				.created(new URI("/api/object-modules/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /object-modules} : Updates an existing objectModule.
	 *
	 * @param objectModuleDTO the objectModuleDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated objectModuleDTO, or with status {@code 400 (Bad Request)}
	 *         if the objectModuleDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the objectModuleDTO couldn't
	 *         be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_OBJECT_MODULE_UPDATE + "\")")
	public ResponseEntity<ObjectModuleDTO> updateObjectModule(@Valid @RequestBody ObjectModuleDTO objectModuleDTO)
			throws URISyntaxException {
		log.debug("REST request to update ObjectModule : {}", objectModuleDTO);
		if (objectModuleDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ObjectModuleDTO result = objectModuleService.update(objectModuleDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				objectModuleDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /object-modules} : get all the objectModules.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of objectModules in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_OBJECT_MODULE_GET_ALL + "\")")
	public ResponseEntity<PageableEntityDTO<ObjectModuleDTO>> getAllObjectModules(ObjectModuleCriteria criteria,
			Pageable pageable) {
		log.debug("REST request to get ObjectModules by criteria: {}", criteria);
		Page<ObjectModuleDTO> page = objectModuleQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<ObjectModuleDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /object-modules/count} : count all the objectModules.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("count")
	public ResponseEntity<Long> countObjectModules(ObjectModuleCriteria criteria) {
		log.debug("REST request to count ObjectModules by criteria: {}", criteria);
		return ResponseEntity.ok().body(objectModuleQueryService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /object-modules/:id} : get the "id" objectModule.
	 *
	 * @param id the id of the objectModuleDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the objectModuleDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_OBJECT_MODULE_GET + "\")")
	public ResponseEntity<ObjectModuleDTO> getObjectModule(@PathVariable Long id) {
		log.debug("REST request to get ObjectModule : {}", id);
		Optional<ObjectModuleDTO> objectModuleDTO = objectModuleQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(objectModuleDTO);
	}

	/**
	 * {@code DELETE  /object-modules/:id} : delete the "id" objectModule.
	 *
	 * @param id the id of the objectModuleDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ROLE_OBJECT_MODULE_DELETE + "\")")
	public ResponseEntity<Void> deleteObjectModule(@PathVariable Long id) {
		log.debug("REST request to delete ObjectModule : {}", id);
		objectModuleService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
	
	@PostMapping("/getApprovalRequirement")
	public void getApprovalRequirement(@RequestBody AuthorizationContainerDTO containerDTO){
		log.debug("REST request to get check Approval : {}", containerDTO);
		objectModuleService.getApprovalRequirement(containerDTO);
	}
}
