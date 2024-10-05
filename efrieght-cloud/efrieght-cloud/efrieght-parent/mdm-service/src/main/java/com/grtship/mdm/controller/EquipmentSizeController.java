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
import com.grtship.core.dto.EquipmentSizeDTO;
import com.grtship.mdm.service.EquipmentSizeService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.EquipmentSize}.
 */
@RestController
@RequestMapping("/api/equipment-sizes")
public class EquipmentSizeController {

	private final Logger log = LoggerFactory.getLogger(EquipmentSizeController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceEquipmentSize";

	@Value("${spring.application.name}")
	private String applicationName;

	private final EquipmentSizeService equipmentSizeService;

	public EquipmentSizeController(EquipmentSizeService equipmentSizeService) {
		this.equipmentSizeService = equipmentSizeService;
	}

	/**
	 * {@code POST  /equipment-sizes} : Create a new equipmentSize.
	 *
	 * @param equipmentSizeDTO the equipmentSizeDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new equipmentSizeDTO, or with status
	 *         {@code 400 (Bad Request)} if the equipmentSize has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<EquipmentSizeDTO> createEquipmentSize(@Valid @RequestBody EquipmentSizeDTO equipmentSizeDTO)
			throws URISyntaxException {
		log.debug("REST request to save EquipmentSize : {}", equipmentSizeDTO);
		if (equipmentSizeDTO.getId() != null) {
			throw new BadRequestAlertException("A new equipmentSize cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		EquipmentSizeDTO result = equipmentSizeService.save(equipmentSizeDTO);
		return ResponseEntity
				.created(new URI("/api/equipment-sizes/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /equipment-sizes} : Updates an existing equipmentSize.
	 *
	 * @param equipmentSizeDTO the equipmentSizeDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated equipmentSizeDTO, or with status
	 *         {@code 400 (Bad Request)} if the equipmentSizeDTO is not valid, or
	 *         with status {@code 500 (Internal Server Error)} if the
	 *         equipmentSizeDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<EquipmentSizeDTO> updateEquipmentSize(@Valid @RequestBody EquipmentSizeDTO equipmentSizeDTO)
			throws URISyntaxException {
		log.debug("REST request to update EquipmentSize : {}", equipmentSizeDTO);
		if (equipmentSizeDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		EquipmentSizeDTO result = equipmentSizeService.save(equipmentSizeDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				equipmentSizeDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /equipment-sizes} : get all the equipmentSizes.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of equipmentSizes in body.
	 */
	@GetMapping
	public List<EquipmentSizeDTO> getAllEquipmentSizes() {
		log.debug("REST request to get all EquipmentSizes");
		return equipmentSizeService.findAll();
	}

	/**
	 * {@code GET  /equipment-sizes/:id} : get the "id" equipmentSize.
	 *
	 * @param id the id of the equipmentSizeDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the equipmentSizeDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<EquipmentSizeDTO> getEquipmentSize(@PathVariable Long id) {
		log.debug("REST request to get EquipmentSize : {}", id);
		Optional<EquipmentSizeDTO> equipmentSizeDTO = equipmentSizeService.findOne(id);
		return ResponseUtil.wrapOrNotFound(equipmentSizeDTO);
	}

	/**
	 * {@code DELETE  /equipment-sizes/:id} : delete the "id" equipmentSize.
	 *
	 * @param id the id of the equipmentSizeDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteEquipmentSize(@PathVariable Long id) {
		log.debug("REST request to delete EquipmentSize : {}", id);
		equipmentSizeService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
