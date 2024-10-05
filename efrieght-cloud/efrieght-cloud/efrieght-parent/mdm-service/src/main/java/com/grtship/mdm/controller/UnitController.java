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
import com.grtship.core.dto.UnitDTO;
import com.grtship.mdm.service.UnitService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Unit}.
 */
@RestController
@RequestMapping("/api/units")
public class UnitController {

	private final Logger log = LoggerFactory.getLogger(UnitController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceUnit";

	@Value("${spring.application.name}")
	private String applicationName;

	private final UnitService unitService;

	public UnitController(UnitService unitService) {
		this.unitService = unitService;
	}

	/**
	 * {@code POST  /units} : Create a new unit.
	 *
	 * @param unitDTO the unitDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new unitDTO, or with status {@code 400 (Bad Request)} if the
	 *         unit has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<UnitDTO> createUnit(@RequestBody UnitDTO unitDTO) throws URISyntaxException {
		log.debug("REST request to save Unit : {}", unitDTO);
		if (unitDTO.getId() != null) {
			throw new BadRequestAlertException("A new unit cannot already have an ID", ENTITY_NAME, "idexists");
		}
		UnitDTO result = unitService.save(unitDTO);
		return ResponseEntity
				.created(new URI("/api/units/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /units} : Updates an existing unit.
	 *
	 * @param unitDTO the unitDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated unitDTO, or with status {@code 400 (Bad Request)} if the
	 *         unitDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the unitDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<UnitDTO> updateUnit(@RequestBody UnitDTO unitDTO) throws URISyntaxException {
		log.debug("REST request to update Unit : {}", unitDTO);
		if (unitDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		UnitDTO result = unitService.save(unitDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, unitDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /units} : get all the units.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of units in body.
	 */
	@GetMapping
	public List<UnitDTO> getAllUnits() {
		log.debug("REST request to get all Units");
		return unitService.findAll();
	}

	/**
	 * {@code GET  /units/:id} : get the "id" unit.
	 *
	 * @param id the id of the unitDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the unitDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<UnitDTO> getUnit(@PathVariable Long id) {
		log.debug("REST request to get Unit : {}", id);
		Optional<UnitDTO> unitDTO = unitService.findOne(id);
		return ResponseUtil.wrapOrNotFound(unitDTO);
	}

	/**
	 * {@code DELETE  /units/:id} : delete the "id" unit.
	 *
	 * @param id the id of the unitDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
		log.debug("REST request to delete Unit : {}", id);
		unitService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
