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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.dto.VesselCreationRequest;
import com.grtship.core.dto.VesselDTO;
import com.grtship.core.dto.VesselDeactivationDto;
import com.grtship.core.dto.VesselUpdateRequest;
import com.grtship.mdm.criteria.VesselCriteria;
import com.grtship.mdm.service.VesselQueryService;
import com.grtship.mdm.service.VesselService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Vessel}.
 */
@RestController
@RequestMapping("/api/vessels")
public class VesselController {

	private static final String DEACTIVATE = "DEACTIVATE";
	private static final String VESSEL_REFERENCE_NAME = "vessel.referenceName";
	private static final String VESSEL_ID_CAN_T_BE_NULL = "Vessel Id can't be null";
	private static final String TYPE_DEACTIVATE_TO_CONFIRM_THE_DEACTIVATION = "Type 'DEACTIVATE' to confirm the deactivation";
	private final Logger log = LoggerFactory.getLogger(VesselController.class);
	private static final String ENTITY_NAME = "masterDataManagementServiceVessel";

	@Value("${spring.application.name}")
	private String applicationName;

	private final VesselService vesselService;

	private final VesselQueryService vesselQueryService;

	public VesselController(VesselService vesselService, VesselQueryService vesselQueryService) {
		this.vesselService = vesselService;
		this.vesselQueryService = vesselQueryService;
	}

	/**
	 * {@code POST  /vessels} : Create a new vessel.
	 *
	 * @param vesselDTO the vesselDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new vesselDTO, or with status {@code 400 (Bad Request)} if
	 *         the vessel has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<VesselDTO> createVessel(@Valid @RequestBody VesselCreationRequest vesselDTO)
			throws URISyntaxException {
		log.debug("REST request to save Vessel : {}", vesselDTO);
		if (vesselDTO.getId() != null) {
			throw new BadRequestAlertException("A new vessel cannot already have an ID", ENTITY_NAME, "idexists");
		}
		VesselDTO result = vesselService.save(vesselDTO);
		return ResponseEntity
				.created(new URI("/api/vessels/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /vessels} : Updates an existing vessel.
	 *
	 * @param vesselDTO the vesselDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated vesselDTO, or with status {@code 400 (Bad Request)} if
	 *         the vesselDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the vesselDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<VesselDTO> updateVessel(@Valid @RequestBody VesselUpdateRequest vesselDTO)
			throws URISyntaxException {
		log.debug("REST request to update Vessel : {}", vesselDTO);
		if (vesselDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		VesselDTO result = vesselService.update(vesselDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vesselDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /vessels} : get all the vessels.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of vessels in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<VesselDTO>> getAllVessels(VesselCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Vessels by criteria: {}", criteria);
		Page<VesselDTO> page = vesselQueryService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<VesselDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /vessels/count} : count all the vessels.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("count")
	public ResponseEntity<Long> countVessels(VesselCriteria criteria) {
		log.debug("REST request to count Vessels by criteria: {}", criteria);
		return ResponseEntity.ok().body(vesselQueryService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /vessels/:id} : get the "id" vessel.
	 *
	 * @param id the id of the vesselDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the vesselDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<VesselDTO> getVessel(@PathVariable Long id) {
		log.debug("REST request to get Vessel : {}", id);
		Optional<VesselDTO> vesselDTO = vesselService.findOne(id);
		return ResponseUtil.wrapOrNotFound(vesselDTO);
	}

	@PostMapping("deactivate")
	public void deactivate(@RequestBody VesselDeactivationDto deactivationDto) {
		log.debug("REST request to deactivate Vessel : {}", deactivationDto);
		if (deactivationDto.getReferenceId() == null) {
			throw new BadRequestAlertException(VESSEL_ID_CAN_T_BE_NULL, ENTITY_NAME, VESSEL_REFERENCE_NAME);
		}
		if (deactivationDto.getType() == null || !deactivationDto.getType().equals(DEACTIVATE)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, TYPE_DEACTIVATE_TO_CONFIRM_THE_DEACTIVATION);
		}
		vesselService.deactivate(deactivationDto);
	}
}
