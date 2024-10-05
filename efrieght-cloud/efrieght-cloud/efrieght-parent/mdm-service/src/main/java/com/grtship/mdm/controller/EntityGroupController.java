package com.grtship.mdm.controller;

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

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.EntityGroupCriteriaDTO;
import com.grtship.core.dto.EntityGroupDTO;
import com.grtship.mdm.service.EntityGroupService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.EntityGroup}.
 */
@RestController
@RequestMapping("/api/entity-group")
public class EntityGroupController {

	private final Logger log = LoggerFactory.getLogger(EntityGroupController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceEntityGroups";

	@Value("${spring.application.name}")
	private String applicationName;

	private final EntityGroupService entityGroupsService;

	public EntityGroupController(EntityGroupService entityGroupsService) {
		this.entityGroupsService = entityGroupsService;
	}

	/**
	 * {@code POST  /entity-groups} : Create a new entityGroups.
	 *
	 * @param entityGroupsDTO the entityGroupsDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new entityGroupsDTO, or with status
	 *         {@code 400 (Bad Request)} if the entityGroups has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<EntityGroupDTO> createEntityGroups(@RequestBody EntityGroupDTO entityGroupsDTO)
			throws URISyntaxException {
		log.debug("REST request to save EntityGroup : {}", entityGroupsDTO);
		if (entityGroupsDTO.getId() != null) {
			throw new BadRequestAlertException("A new entityGroups cannot already have an ID", ENTITY_NAME, "idexists");
		}
		EntityGroupDTO result = entityGroupsService.save(entityGroupsDTO);
		return ResponseEntity
				.created(new URI("/api/entity-group/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /entity-groups} : Updates an existing entityGroups.
	 *
	 * @param entityGroupsDTO the entityGroupsDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated entityGroupsDTO, or with status {@code 400 (Bad Request)}
	 *         if the entityGroupsDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the entityGroupsDTO couldn't
	 *         be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<EntityGroupDTO> updateEntityGroups(@RequestBody EntityGroupDTO entityGroupsDTO)
			throws URISyntaxException {
		log.debug("REST request to update EntityGroup : {}", entityGroupsDTO);
		if (entityGroupsDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		EntityGroupDTO result = entityGroupsService.save(entityGroupsDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				entityGroupsDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /entity-groups} : get all the entityGroups.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of entityGroups in body.
	 */
	@GetMapping
	public List<EntityGroupDTO> getAllEntityGroups(EntityGroupCriteriaDTO entityGroupsCriteria) {
		log.debug("REST request to get all EntityGroups");
		return entityGroupsService.findAll(entityGroupsCriteria);
	}

	/**
	 * {@code GET  /entity-groups/:id} : get the "id" entityGroups.
	 *
	 * @param id the id of the entityGroupsDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the entityGroupsDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<EntityGroupDTO> getEntityGroups(@PathVariable Long id) {
		log.debug("REST request to get EntityGroup : {}", id);
		Optional<EntityGroupDTO> entityGroupsDTO = entityGroupsService.findOne(id);
		return ResponseUtil.wrapOrNotFound(entityGroupsDTO);
	}

	/**
	 * {@code DELETE  /entity-groups/:id} : delete the "id" entityGroups.
	 *
	 * @param id the id of the entityGroupsDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteEntityGroups(@PathVariable Long id) {
		log.debug("REST request to delete EntityGroup : {}", id);
		entityGroupsService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
