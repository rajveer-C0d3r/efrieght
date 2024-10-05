package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.annotation.Authorize;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.core.enumeration.ModuleName;
import com.grtship.mdm.criteria.DestinationCriteria;
import com.grtship.mdm.service.DestinationQueryService;
import com.grtship.mdm.service.DestinationService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Destination}.
 */
@RestController
@RequestMapping("/api/destination")
public class DestinationController {

	private static final String UNABLE_TO_DELETE_THIS_DESTINATION_HAS_BEEN_USED_IN_SYSTEM = "Unable to Delete. This destination has been used in system.";

	private static final String DESTINATION_DELETE = "Destination Delete";

	private static final String DESTINATION_MANAGEMENT = "Destination Management";

	private final Logger log = LoggerFactory.getLogger(DestinationController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceDestination";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private DestinationService destinationService;

	@Autowired
	private DestinationQueryService destinationQueryService;

	/**
	 * {@code POST  /destinations} : Create a new destination.
	 *
	 * @param destinationDTO the destinationDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new destinationDTO, or with status {@code 400 (Bad Request)}
	 *         if the destination has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESTINATION_ADD + "\")")
	@Authorize(moduleName = ModuleName.GROUP)
	public ResponseEntity<DestinationDTO> createDestination(@Valid @RequestBody DestinationDTO destinationDTO)
			throws URISyntaxException {
		log.debug("REST request to save Destination : {}", destinationDTO);
		if (destinationDTO.getId() != null) {
			throw new BadRequestAlertException("A new destination cannot already have an ID", ENTITY_NAME, "idexists");
		}
		DestinationDTO result = destinationService.save(destinationDTO);
		return ResponseEntity
				.created(new URI("/api/destination/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /destinations} : Updates an existing destination.
	 *
	 * @param destinationDTO the destinationDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated destinationDTO, or with status {@code 400 (Bad Request)}
	 *         if the destinationDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the destinationDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESTINATION_EDIT + "\")")
	public ResponseEntity<DestinationDTO> updateDestination(@Valid @RequestBody DestinationDTO destinationDTO)
			throws URISyntaxException {
		log.debug("REST request to update Destination : {}", destinationDTO);
		if (destinationDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		DestinationDTO result = destinationService.update(destinationDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				destinationDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /destinations} : get all the destinations.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of destinations in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESTINATION_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<DestinationDTO>> getAllDestinations(DestinationCriteria destinationCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Destinations");
		Page<DestinationDTO> page = destinationQueryService.findByCriteria(destinationCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<DestinationDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /destinations/:id} : get the "id" destination.
	 *
	 * @param id the id of the destinationDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the destinationDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESTINATION_VIEW + "\")")
	public ResponseEntity<DestinationDTO> getDestination(@PathVariable Long id) {
		log.debug("REST request to get Destination : {}", id);
		Optional<DestinationDTO> destinationDTO = destinationQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(destinationDTO);
	}

	@GetMapping("getDestinationByCountryId/{countryId}")
	public ResponseEntity<List<DestinationDTO>> getDestinationByCountryId(@PathVariable Long countryId) {
		log.debug("REST request to get Destination by Country id: {}", countryId);
		List<DestinationDTO> destinationDTO = destinationQueryService.getDestinationByCountryId(countryId);
		return ResponseEntity.ok().body(destinationDTO);
	}

	@GetMapping("getDestinationStateId/{stateId}")
	public ResponseEntity<List<DestinationDTO>> getDestinationStateId(@PathVariable Long stateId) {
		log.debug("REST request to get Destination by State id: {}", stateId);
		List<DestinationDTO> destinationDTO = destinationQueryService.getDestinationStateId(stateId);
		return ResponseEntity.ok().body(destinationDTO);
	}

	@GetMapping("getCities/{countryId}")
	public ResponseEntity<List<DestinationDTO>> getCities(@PathVariable Long countryId) {
		log.debug("REST request to get Cities ");
		List<DestinationDTO> destinationDTO = destinationQueryService.getCities(countryId);
		return ResponseEntity.ok().body(destinationDTO);
	}

	@GetMapping("getPorts/{cityId}")
	public ResponseEntity<List<BaseDTO>> getPorts(@PathVariable Long cityId) {
		log.debug("REST request to get Ports ");
		List<BaseDTO> destinationDTO = destinationQueryService.getPorts(cityId);
		return ResponseEntity.ok().body(destinationDTO);
	}

	/**
	 * {@code DELETE  /destinations/:id} : delete the "id" destination.
	 *
	 * @param id the id of the destinationDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.DESTINATION_DELETE + "\")")
	public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
		log.debug("REST request to delete Destination : {}", id);
		try {
			destinationService.delete(id);
		} catch (Exception ex) {
			throw new BadRequestAlertException(UNABLE_TO_DELETE_THIS_DESTINATION_HAS_BEEN_USED_IN_SYSTEM,
					DESTINATION_MANAGEMENT, DESTINATION_DELETE);
		}
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("getDestinationByIdList")
	public List<DestinationDTO> getDestinationsByIds(@RequestBody List<Long> idList) {
		log.debug("REST request to get Destinations by id list: {}", idList);
		return destinationQueryService.getDestinationsByIdList(idList);
	}
}
