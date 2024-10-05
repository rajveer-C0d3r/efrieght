package com.grtship.account.controller;

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

import com.grtship.account.service.TdsRateService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.TdsRateDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.TdsRate}.
 */
@RestController
@RequestMapping("/api")
public class TdsRateController {

	private final Logger log = LoggerFactory.getLogger(TdsRateController.class);

	private static final String ENTITY_NAME = "accountServiceTdsRate";

	@Value("${spring.application.name}")
	private String applicationName;

	private final TdsRateService tdsRateService;

	public TdsRateController(TdsRateService tdsRateService) {
		this.tdsRateService = tdsRateService;
	}

	/**
	 * {@code POST  /tds-rates} : Create a new tdsRate.
	 *
	 * @param tdsRateDTO the tdsRateDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new tdsRateDTO, or with status {@code 400 (Bad Request)} if
	 *         the tdsRate has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/tds-rates")
	public ResponseEntity<TdsRateDTO> createTdsRate(@Valid @RequestBody TdsRateDTO tdsRateDTO)
			throws URISyntaxException {
		log.debug("REST request to save TdsRate : {}", tdsRateDTO);
		if (tdsRateDTO.getId() != null) {
			throw new BadRequestAlertException("A new tdsRate cannot already have an ID", ENTITY_NAME, "idexists");
		}
		TdsRateDTO result = tdsRateService.save(tdsRateDTO);
		return ResponseEntity
				.created(new URI("/api/tds-rates/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /tds-rates} : Updates an existing tdsRate.
	 *
	 * @param tdsRateDTO the tdsRateDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated tdsRateDTO, or with status {@code 400 (Bad Request)} if
	 *         the tdsRateDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the tdsRateDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/tds-rates")
	public ResponseEntity<TdsRateDTO> updateTdsRate(@Valid @RequestBody TdsRateDTO tdsRateDTO)
			throws URISyntaxException {
		log.debug("REST request to update TdsRate : {}", tdsRateDTO);
		if (tdsRateDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		TdsRateDTO result = tdsRateService.save(tdsRateDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tdsRateDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /tds-rates} : get all the tdsRates.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of tdsRates in body.
	 */
	@GetMapping("/tds-rates")
	public List<TdsRateDTO> getAllTdsRates() {
		log.debug("REST request to get all TdsRates");
		return tdsRateService.findAll();
	}

	/**
	 * {@code GET  /tds-rates/:id} : get the "id" tdsRate.
	 *
	 * @param id the id of the tdsRateDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the tdsRateDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/tds-rates/{id}")
	public ResponseEntity<TdsRateDTO> getTdsRate(@PathVariable Long id) {
		log.debug("REST request to get TdsRate : {}", id);
		Optional<TdsRateDTO> tdsRateDTO = tdsRateService.findOne(id);
		return ResponseUtil.wrapOrNotFound(tdsRateDTO);
	}

	/**
	 * {@code DELETE  /tds-rates/:id} : delete the "id" tdsRate.
	 *
	 * @param id the id of the tdsRateDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("/tds-rates/{id}")
	public ResponseEntity<Void> deleteTdsRate(@PathVariable Long id) {
		log.debug("REST request to delete TdsRate : {}", id);
		tdsRateService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}
}
