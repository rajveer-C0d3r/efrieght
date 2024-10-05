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
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.SectorDTO;
import com.grtship.mdm.criteria.SectorCriteria;
import com.grtship.mdm.service.SectorQueryService;
import com.grtship.mdm.service.SectorService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Sector}.
 */
@RestController
@RequestMapping("/api/sectors")
public class SectorController {

	private final Logger log = LoggerFactory.getLogger(SectorController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceSector";

	@Value("${spring.application.name}")
	private String applicationName;

	private final SectorService sectorService;

	private final SectorQueryService sectorQueryService;

	public SectorController(SectorService sectorService, SectorQueryService sectorQueryService) {
		this.sectorService = sectorService;
		this.sectorQueryService = sectorQueryService;
	}

	/**
	 * {@code POST  /sectors} : Create a new sector.
	 *
	 * @param sectorDTO the sectorDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new sectorDTO, or with status {@code 400 (Bad Request)} if
	 *         the sector has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<SectorDTO> createSector(@Valid @RequestBody SectorDTO sectorDTO) throws URISyntaxException {
		log.debug("REST request to save Sector : {}", sectorDTO);
		if (sectorDTO.getId() != null) {
			throw new BadRequestAlertException("A new sector cannot already have an ID", ENTITY_NAME, "idexists");
		}
		SectorDTO result = sectorService.save(sectorDTO);
		return ResponseEntity
				.created(new URI("/api/sectors/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /sectors} : Updates an existing sector.
	 *
	 * @param sectorDTO the sectorDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated sectorDTO, or with status {@code 400 (Bad Request)} if
	 *         the sectorDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the sectorDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<SectorDTO> updateSector(@Valid @RequestBody SectorDTO sectorDTO) throws URISyntaxException {
		log.debug("REST request to update Sector : {}", sectorDTO);
		if (sectorDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		SectorDTO result = sectorService.save(sectorDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sectorDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /sectors} : get all the sectors.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of sectors in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<SectorDTO>> getAllSectors(SectorCriteria sectorCriteria,
			Pageable pageable) {
		log.debug("REST request to get all Sectors");
		Page<SectorDTO> page = sectorQueryService.findByCriteria(sectorCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<SectorDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /sectors/:id} : get the "id" sector.
	 *
	 * @param id the id of the sectorDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the sectorDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<SectorDTO> getSector(@PathVariable Long id) {
		log.debug("REST request to get Sector : {}", id);
		Optional<SectorDTO> sectorDTO = sectorService.findOne(id);
		return ResponseUtil.wrapOrNotFound(sectorDTO);
	}

	/**
	 * {@code DELETE  /sectors/:id} : delete the "id" sector.
	 *
	 * @param id the id of the sectorDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteSector(@PathVariable Long id) {
		log.debug("REST request to delete Sector : {}", id);
		sectorService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@GetMapping("getByCountryId/{countryId}")
	public ResponseEntity<SectorDTO> getByCountryId(@PathVariable(name = "countryId") Long countryId) {
		log.debug("Request to get Sector name by country id {}", countryId);
		Optional<SectorDTO> sectorDTO = sectorService.getByCountryId(countryId);
		return ResponseUtil.wrapOrNotFound(sectorDTO);
	}

}
