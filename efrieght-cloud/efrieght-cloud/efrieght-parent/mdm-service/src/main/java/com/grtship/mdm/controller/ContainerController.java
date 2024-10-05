package com.grtship.mdm.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.ContainerDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.service.ContainerService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Container}.
 */
@RestController
@RequestMapping("/api/containers")
public class ContainerController {

	private final Logger log = LoggerFactory.getLogger(ContainerController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceContainer";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ContainerService containerService;

	public ContainerController(ContainerService containerService) {
		this.containerService = containerService;
	}

	/**
	 * {@code POST  /containers} : Create a new container.
	 *
	 * @param containerDTO the containerDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new containerDTO, or with status {@code 400 (Bad Request)}
	 *         if the container has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<ContainerDTO> createContainer(@Valid @RequestBody ContainerDTO containerDTO)
			throws URISyntaxException {
		log.debug("REST request to save Container : {}", containerDTO);
		if (containerDTO.getId() != null) {
			throw new BadRequestAlertException("A new container cannot already have an ID", ENTITY_NAME, "idexists");
		}
		ContainerDTO result = containerService.save(containerDTO);
		return ResponseEntity
				.created(new URI("/api/containers/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /containers} : Updates an existing container.
	 *
	 * @param containerDTO the containerDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated containerDTO, or with status {@code 400 (Bad Request)} if
	 *         the containerDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the containerDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<ContainerDTO> updateContainer(@Valid @RequestBody ContainerDTO containerDTO)
			throws URISyntaxException {
		log.debug("REST request to update Container : {}", containerDTO);
		if (containerDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ContainerDTO result = containerService.save(containerDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, containerDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /containers} : get all the containers.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of containers in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<ContainerDTO>> getAllContainers(Pageable pageable) {
		log.debug("REST request to get a page of Containers");
		Page<ContainerDTO> page = containerService.findAll(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<ContainerDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /containers/:id} : get the "id" container.
	 *
	 * @param id the id of the containerDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the containerDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<ContainerDTO> getContainer(@PathVariable Long id) {
		log.debug("REST request to get Container : {}", id);
		Optional<ContainerDTO> containerDTO = containerService.findOne(id);
		return ResponseUtil.wrapOrNotFound(containerDTO);
	}

	/**
	 * {@code DELETE  /containers/:id} : delete the "id" container.
	 *
	 * @param id the id of the containerDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */

	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteContainer(@PathVariable Long id) {
		log.debug("REST request to delete Container : {}", id);
		containerService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
