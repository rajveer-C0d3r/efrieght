package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.criteria.CurrencyCriteria;
import com.grtship.mdm.service.CurrencyService;
import com.grtship.mdm.serviceimpl.CurrencyFilterService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Currency}.
 */
@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

	private static final String UNABLE_TO_DELETE_THIS_CURRENCY_HAS_BEEN_USED_IN_OTHER_MASTERS = "Unable to delete. This Currency Has Been Used In Other Masters.";

	private static final String CURRENCY_MANAGEMENT = "currencyManagement";

	private final Logger log = LoggerFactory.getLogger(CurrencyController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceCurrency";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private CurrencyFilterService currencyFilterService;

	/**
	 * {@code POST  /currencies} : Create a new currency.
	 *
	 * @param currencyDTO the currencyDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new currencyDTO, or with status {@code 400 (Bad Request)} if
	 *         the currency has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.CURRENCY_ADD + "\")")
	public ResponseEntity<CurrencyDTO> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO)
			throws URISyntaxException {
		log.debug("REST request to save Currency : {}", currencyDTO);
		if (currencyDTO.getId() != null) {
			throw new BadRequestAlertException("A new currency cannot already have an ID", ENTITY_NAME, "idexists");
		}
		CurrencyDTO result = currencyService.save(currencyDTO);
		return ResponseEntity
				.created(new URI("/api/currencies/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /currencies} : Updates an existing currency.
	 *
	 * @param currencyDTO the currencyDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated currencyDTO, or with status {@code 400 (Bad Request)} if
	 *         the currencyDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the currencyDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.CURRENCY_EDIT + "\")")
	public ResponseEntity<CurrencyDTO> updateCurrency(@Valid @RequestBody CurrencyDTO currencyDTO)
			throws URISyntaxException {
		log.debug("REST request to update Currency : {}", currencyDTO);
		if (currencyDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		CurrencyDTO result = currencyService.save(currencyDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, currencyDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /currencies} : get all the currencies.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of currencies in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.CURRENCY_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<CurrencyDTO>> getAllCurrencies(CurrencyCriteria currencyCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Currencies");
		Page<CurrencyDTO> page = currencyFilterService.findByCriteria(currencyCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<CurrencyDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /currencies/:id} : get the "id" currency.
	 *
	 * @param id the id of the currencyDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the currencyDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.CURRENCY_VIEW + "\")")
	public ResponseEntity<CurrencyDTO> getCurrency(@PathVariable Long id) {
		log.debug("REST request to get Currency : {}", id);
		Optional<CurrencyDTO> currencyDTO = currencyService.findOne(id);
		return ResponseUtil.wrapOrNotFound(currencyDTO);
	}

	/**
	 * {@code DELETE  /currencies/:id} : delete the "id" currency.
	 *
	 * @param id the id of the currencyDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */

	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.CURRENCY_DELETE + "\")")
	public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
		log.debug("REST request to delete Currency : {}", id);
		try {
			currencyService.delete(id);
		} catch (Exception ex) {
			throw new BadRequestAlertException(UNABLE_TO_DELETE_THIS_CURRENCY_HAS_BEEN_USED_IN_OTHER_MASTERS,
					CURRENCY_MANAGEMENT, CURRENCY_MANAGEMENT);
		}
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	@PostMapping("/getAllCurrenciesByIdList")
	public Map<Long, CurrencyDTO> getAllCurrenciesIdList(@RequestBody List<Long> currencyIdList) {
		return currencyService.getAllCurrenciesIdList(currencyIdList);
	}
	
	@GetMapping("/getAllCurrencies")
	public ResponseEntity<List<CurrencyDTO>> getAllCurrencies(CurrencyCriteria currencyCriteria) {
		log.debug("REST request to get a page of Currencies");
		List<CurrencyDTO> currencyDtos = currencyFilterService.findByCriteria(currencyCriteria);
		return ResponseEntity.ok().body(currencyDtos);
	}
}
