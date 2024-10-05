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
import com.grtship.core.dto.EquipmentDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.service.EquipmentService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Equipment}.
 */
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

	private final Logger log = LoggerFactory.getLogger(EquipmentController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceEquipment";

	@Value("${spring.application.name}")
	private String applicationName;

	private final EquipmentService equipmentService;

	public EquipmentController(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	/**
	 * {@code POST  /equipment} : Create a new equipment.
	 *
	 * @param equipmentDTO the equipmentDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new equipmentDTO, or with status {@code 400 (Bad Request)}
	 *         if the equipment has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO)
			throws URISyntaxException {
		log.debug("REST request to save Equipment : {}", equipmentDTO);
		if (equipmentDTO.getId() != null) {
			throw new BadRequestAlertException("A new equipment cannot already have an ID", ENTITY_NAME, "idexists");
		}
		EquipmentDTO result = equipmentService.save(equipmentDTO);
		return ResponseEntity
				.created(new URI("/api/equipment/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /equipment} : Updates an existing equipment.
	 *
	 * @param equipmentDTO the equipmentDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated equipmentDTO, or with status {@code 400 (Bad Request)} if
	 *         the equipmentDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the equipmentDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<EquipmentDTO> updateEquipment(@Valid @RequestBody EquipmentDTO equipmentDTO)
			throws URISyntaxException {
		log.debug("REST request to update Equipment : {}", equipmentDTO);
		if (equipmentDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		EquipmentDTO result = equipmentService.save(equipmentDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /equipment} : get all the equipment.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of equipment in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<EquipmentDTO>> getAllEquipment(Pageable pageable) {
		log.debug("REST request to get a page of Equipment");
		Page<EquipmentDTO> page = equipmentService.findAll(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<EquipmentDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /equipment/:id} : get the "id" equipment.
	 *
	 * @param id the id of the equipmentDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the equipmentDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable Long id) {
		log.debug("REST request to get Equipment : {}", id);
		Optional<EquipmentDTO> equipmentDTO = equipmentService.findOne(id);
		return ResponseUtil.wrapOrNotFound(equipmentDTO);
	}

	/**
	 * {@code DELETE  /equipment/:id} : delete the "id" equipment.
	 *
	 * @param id the id of the equipmentDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
		log.debug("REST request to delete Equipment : {}", id);
		equipmentService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
