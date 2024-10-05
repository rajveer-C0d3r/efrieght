package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
import com.grtship.core.dto.CreditTermsDTO;
import com.grtship.mdm.service.CreditTermsService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.CreditTerms}.
 */
@RestController
@RequestMapping("/credit-terms")
public class CreditTermsController {

	private final Logger log = LoggerFactory.getLogger(CreditTermsController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceCreditTerms";

	@Value("${spring.application.name}")
	private String applicationName;

	private final CreditTermsService creditTermsService;

	public CreditTermsController(CreditTermsService creditTermsService) {
		this.creditTermsService = creditTermsService;
	}

	/**
	 * {@code POST  /credit-terms} : Create a new creditTerms.
	 *
	 * @param creditTermsDTO the creditTermsDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new creditTermsDTO, or with status {@code 400 (Bad Request)}
	 *         if the creditTerms has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<CreditTermsDTO> createCreditTerms(@RequestBody CreditTermsDTO creditTermsDTO)
			throws URISyntaxException {
		log.debug("REST request to save CreditTerms : {}", creditTermsDTO);
		if (creditTermsDTO.getId() != null) {
			throw new BadRequestAlertException("A new creditTerms cannot already have an ID", ENTITY_NAME, "idexists");
		}
		CreditTermsDTO result = creditTermsService.save(creditTermsDTO);
		return ResponseEntity
				.created(new URI("/api/credit-terms/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /credit-terms} : Updates an existing creditTerms.
	 *
	 * @param creditTermsDTO the creditTermsDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated creditTermsDTO, or with status {@code 400 (Bad Request)}
	 *         if the creditTermsDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the creditTermsDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<CreditTermsDTO> updateCreditTerms(@RequestBody CreditTermsDTO creditTermsDTO)
			throws URISyntaxException {
		log.debug("REST request to update CreditTerms : {}", creditTermsDTO);
		if (creditTermsDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		CreditTermsDTO result = creditTermsService.save(creditTermsDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				creditTermsDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /credit-terms} : get all the creditTerms.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of creditTerms in body.
	 */
	@GetMapping
	public ResponseEntity<List<CreditTermsDTO>> getAllCreditTerms(Pageable pageable) {
		log.debug("REST request to get a page of CreditTerms");
		Page<CreditTermsDTO> page = creditTermsService.findAll(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /credit-terms/:id} : get the "id" creditTerms.
	 *
	 * @param id the id of the creditTermsDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the creditTermsDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<CreditTermsDTO> getCreditTerms(@PathVariable Long id) {
		log.debug("REST request to get CreditTerms : {}", id);
		Optional<CreditTermsDTO> creditTermsDTO = creditTermsService.findOne(id);
		return ResponseUtil.wrapOrNotFound(creditTermsDTO);
	}

	/**
	 * {@code DELETE  /credit-terms/:id} : delete the "id" creditTerms.
	 *
	 * @param id the id of the creditTermsDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */

	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteCreditTerms(@PathVariable Long id) {
		log.debug("REST request to delete CreditTerms : {}", id);
		creditTermsService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}
}
