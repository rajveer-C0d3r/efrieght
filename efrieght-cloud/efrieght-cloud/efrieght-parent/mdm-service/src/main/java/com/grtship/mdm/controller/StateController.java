package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
import com.grtship.core.dto.StateDTO;
import com.grtship.mdm.service.StateService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.State}.
 */
@RestController
@RequestMapping("/api/states")
public class StateController {

	private final Logger log = LoggerFactory.getLogger(StateController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceState";

	@Value("${spring.application.name}")
	private String applicationName;

	private final StateService stateService;

	public StateController(StateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * {@code POST  /states} : Create a new state.
	 *
	 * @param stateDTO the stateDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new stateDTO, or with status {@code 400 (Bad Request)} if
	 *         the state has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<StateDTO> createState(@Valid @RequestBody StateDTO stateDTO) throws URISyntaxException {
		log.debug("REST request to save State : {}", stateDTO);
		if (stateDTO.getId() != null) {
			throw new BadRequestAlertException("A new state cannot already have an ID", ENTITY_NAME, "idexists");
		}
		StateDTO result = stateService.save(stateDTO);
		return ResponseEntity
				.created(new URI("/api/states/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /states} : Updates an existing state.
	 *
	 * @param stateDTO the stateDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated stateDTO, or with status {@code 400 (Bad Request)} if the
	 *         stateDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the stateDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<StateDTO> updateState(@Valid @RequestBody StateDTO stateDTO) throws URISyntaxException {
		log.debug("REST request to update State : {}", stateDTO);
		if (stateDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		StateDTO result = stateService.save(stateDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stateDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /states} : get all the states.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of states in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<StateDTO>> getAllStates(Pageable pageable) {
		log.debug("REST request to get a page of States");
		Page<StateDTO> page = stateService.findAll(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<StateDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /states/:id} : get the "id" state.
	 *
	 * @param id the id of the stateDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the stateDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<StateDTO> getState(@PathVariable Long id) {
		log.debug("REST request to get State : {}", id);
		Optional<StateDTO> stateDTO = stateService.findOne(id);
		return ResponseUtil.wrapOrNotFound(stateDTO);
	}

	/**
	 * {@code DELETE  /states/:id} : delete the "id" state.
	 *
	 * @param id the id of the stateDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteState(@PathVariable Long id) {
		log.debug("REST request to delete State : {}", id);
		stateService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("getStatesByIds")
	public List<StateDTO> getStatesByIds(@RequestBody List<Long> ids) {
		log.debug("REST request to get States by id list: {}", ids);
		return stateService.getStatesByIds(ids);
	}

	@GetMapping("getByCountryId/{countryId}")
	public ResponseEntity<List<StateDTO>> getByCountryId(
			@PathVariable(name = "countryId", required = true) Long countryId) {
		log.debug("Request to get states based on country id");
		List<StateDTO> states = stateService.getByCountryId(countryId);
		return ResponseEntity.ok().body(states);
	}

}
