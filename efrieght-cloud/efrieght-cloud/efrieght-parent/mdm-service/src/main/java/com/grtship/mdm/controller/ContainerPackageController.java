package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

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
import com.grtship.core.dto.ContainerPackageDTO;
import com.grtship.mdm.service.ContainerPackageService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.grt.efreight.domain.ContainerPackage}.
 */
@RestController
@RequestMapping("/api/container-packages")
public class ContainerPackageController {

	private final Logger log = LoggerFactory.getLogger(ContainerPackageController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceContainerPackage";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ContainerPackageService containerPackageService;

	public ContainerPackageController(ContainerPackageService containerPackageService) {
		this.containerPackageService = containerPackageService;
	}

	/**
	 * {@code POST  /container-packages} : Create a new containerPackage.
	 *
	 * @param containerPackageDTO the containerPackageDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new containerPackageDTO, or with status
	 *         {@code 400 (Bad Request)} if the containerPackage has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<ContainerPackageDTO> createContainerPackage(
			@Valid @RequestBody ContainerPackageDTO containerPackageDTO) throws URISyntaxException {
		log.debug("REST request to save ContainerPackage : {}", containerPackageDTO);
		if (containerPackageDTO.getId() != null) {
			throw new BadRequestAlertException("A new containerPackage cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		ContainerPackageDTO result = containerPackageService.save(containerPackageDTO);
		return ResponseEntity
				.created(new URI("/api/container-packages/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /container-packages} : Updates an existing containerPackage.
	 *
	 * @param containerPackageDTO the containerPackageDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated containerPackageDTO, or with status
	 *         {@code 400 (Bad Request)} if the containerPackageDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         containerPackageDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<ContainerPackageDTO> updateContainerPackage(
			@Valid @RequestBody ContainerPackageDTO containerPackageDTO) throws URISyntaxException {
		log.debug("REST request to update ContainerPackage : {}", containerPackageDTO);
		if (containerPackageDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ContainerPackageDTO result = containerPackageService.save(containerPackageDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				containerPackageDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /container-packages} : get all the containerPackages.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of containerPackages in body.
	 */
	@GetMapping
	public List<ContainerPackageDTO> getAllContainerPackages() {
		log.debug("REST request to get all ContainerPackages");
		return containerPackageService.findAll();
	}

	/**
	 * {@code GET  /container-packages/:id} : get the "id" containerPackage.
	 *
	 * @param id the id of the containerPackageDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the containerPackageDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<ContainerPackageDTO> getContainerPackage(@PathVariable Long id) {
		log.debug("REST request to get ContainerPackage : {}", id);
		Optional<ContainerPackageDTO> containerPackageDTO = containerPackageService.findOne(id);
		return ResponseUtil.wrapOrNotFound(containerPackageDTO);
	}

	/**
	 * {@code DELETE  /container-packages/:id} : delete the "id" containerPackage.
	 *
	 * @param id the id of the containerPackageDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	

	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteContainerPackage(@PathVariable Long id) {
		log.debug("REST request to delete ContainerPackage : {}", id);
		containerPackageService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
