package com.grtship.account.controller;

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

import com.grtship.account.service.impl.ShipmentReferenceServiceImpl;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.ShipmentReferenceDTO;

/**
 * REST controller for managing
 * {@link com.grt.efreight.account.domain.ShipmentReference}.
 */
@RestController
@RequestMapping("/api")
public class ShipmentReferenceController {

	private final Logger log = LoggerFactory.getLogger(ShipmentReferenceController.class);

	private static final String ENTITY_NAME = "accountServiceShipmentReference";

	@Value("${spring.application.name}")
	private String applicationName;

	private final ShipmentReferenceServiceImpl shipmentReferenceService;

	public ShipmentReferenceController(ShipmentReferenceServiceImpl shipmentReferenceService) {
		this.shipmentReferenceService = shipmentReferenceService;
	}

	/**
	 * {@code POST  /shipment-references} : Create a new shipmentReference.
	 *
	 * @param shipmentReferenceDTO the shipmentReferenceDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new shipmentReferenceDTO, or with status
	 *         {@code 400 (Bad Request)} if the shipmentReference has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/shipment-references")
	public ResponseEntity<ShipmentReferenceDTO> createShipmentReference(
			@RequestBody ShipmentReferenceDTO shipmentReferenceDTO) throws URISyntaxException {
		log.debug("REST request to save ShipmentReference : {}", shipmentReferenceDTO);
		if (shipmentReferenceDTO.getId() != null) {
			throw new BadRequestAlertException("A new shipmentReference cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		ShipmentReferenceDTO result = shipmentReferenceService.save(shipmentReferenceDTO);
		return ResponseEntity
				.created(new URI("/api/shipment-references/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /shipment-references} : Updates an existing shipmentReference.
	 *
	 * @param shipmentReferenceDTO the shipmentReferenceDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated shipmentReferenceDTO, or with status
	 *         {@code 400 (Bad Request)} if the shipmentReferenceDTO is not valid,
	 *         or with status {@code 500 (Internal Server Error)} if the
	 *         shipmentReferenceDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/shipment-references")
	public ResponseEntity<ShipmentReferenceDTO> updateShipmentReference(
			@RequestBody ShipmentReferenceDTO shipmentReferenceDTO) throws URISyntaxException {
		log.debug("REST request to update ShipmentReference : {}", shipmentReferenceDTO);
		if (shipmentReferenceDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		ShipmentReferenceDTO result = shipmentReferenceService.save(shipmentReferenceDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME,
				shipmentReferenceDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /shipment-references} : get all the shipmentReferences.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of shipmentReferences in body.
	 */
	@GetMapping("/shipment-references")
	public List<ShipmentReferenceDTO> getAllShipmentReferences() {
		log.debug("REST request to get all ShipmentReferences");
		return shipmentReferenceService.findAll();
	}

	/**
	 * {@code GET  /shipment-references/:id} : get the "id" shipmentReference.
	 *
	 * @param id the id of the shipmentReferenceDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the shipmentReferenceDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/shipment-references/{id}")
	public ResponseEntity<ShipmentReferenceDTO> getShipmentReference(@PathVariable Long id) {
		log.debug("REST request to get ShipmentReference : {}", id);
		Optional<ShipmentReferenceDTO> shipmentReferenceDTO = shipmentReferenceService.findOne(id);
		return ResponseUtil.wrapOrNotFound(shipmentReferenceDTO);
	}

	/**
	 * {@code DELETE  /shipment-references/:id} : delete the "id" shipmentReference.
	 *
	 * @param id the id of the shipmentReferenceDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("/shipment-references/{id}")
	public ResponseEntity<Void> deleteShipmentReference(@PathVariable Long id) {
		log.debug("REST request to delete ShipmentReference : {}", id);
		shipmentReferenceService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}
}
