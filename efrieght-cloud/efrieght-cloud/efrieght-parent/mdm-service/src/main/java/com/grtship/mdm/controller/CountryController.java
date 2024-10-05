package com.grtship.mdm.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.KeyLabelBeanDTO;
import com.grtship.core.dto.PageableEntityDTO;
import com.grtship.mdm.criteria.CountryCriteria;
import com.grtship.mdm.service.CountryService;
import com.grtship.mdm.util.HeaderUtil;
import com.grtship.mdm.util.PaginationUtil;
import com.grtship.mdm.util.ResponseUtil;

/**
 * REST controller for managing {@link com.grt.efreight.domain.Country}.
 */
@RestController
@RequestMapping("/api/countries")
public class CountryController {

	private static final String IDNULL = "idnull";

	private static final String INVALID_ID = "Invalid id";

	private static final String IDEXISTS = "idexists";

	private static final String A_NEW_COUNTRY_CANNOT_ALREADY_HAVE_AN_ID = "A new country cannot already have an ID";

	private static final String COUNTRY_CAN_T_BE_DELETED = "Country can't be deleted.";

	private final Logger log = LoggerFactory.getLogger(CountryController.class);

	private static final String ENTITY_NAME = "masterDataManagementServiceCountry";

	@Value("${spring.application.name}")
	private String applicationName;

	private final CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}

	/**
	 * {@code POST  /countries} : Create a new country.
	 *
	 * @param countryDTO the countryDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new countryDTO, or with status {@code 400 (Bad Request)} if
	 *         the country has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COUNTRY_ADD + "\")")
	public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO countryDTO)
			throws URISyntaxException {
		log.debug("REST request to save Country : {}", countryDTO);
		if (countryDTO.getId() != null) {
			throw new BadRequestAlertException(A_NEW_COUNTRY_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME, IDEXISTS);
		}
		CountryDTO result = countryService.save(countryDTO);
		return ResponseEntity
				.created(new URI("/api/countries/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /countries} : Updates an existing country.
	 *
	 * @param countryDTO the countryDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated countryDTO, or with status {@code 400 (Bad Request)} if
	 *         the countryDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the countryDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COUNTRY_EDIT + "\")")
	public ResponseEntity<CountryDTO> updateCountry(@Valid @RequestBody CountryDTO countryDTO)
			throws URISyntaxException {
		log.debug("REST request to update Country : {}", countryDTO);
		if (countryDTO.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		CountryDTO result = countryService.save(countryDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, countryDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /countries} : get all the countries.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of countries in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COUNTRY_VIEW + "\")")
	public ResponseEntity<PageableEntityDTO<CountryDTO>> getAllCountries(CountryCriteria countryCriteria,
			Pageable pageable) {
		log.debug("REST request to get a page of Countries");
		Page<CountryDTO> page = countryService.findAll(countryCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<CountryDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /countries/:id} : get the "id" country.
	 *
	 * @param id the id of the countryDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the countryDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COUNTRY_VIEW + "\")")
	public ResponseEntity<CountryDTO> getCountry(@PathVariable Long id) {
		log.debug("REST request to get Country : {}", id);
		Optional<CountryDTO> countryDTO = countryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(countryDTO);
	}

	/**
	 * {@code DELETE  /countries/:id} : delete the "id" country.
	 *
	 * @param id the id of the countryDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */

	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.COUNTRY_DELETE + "\")")
	public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
		log.debug("REST request to delete Country : {}", id);
		throw new BadRequestAlertException(COUNTRY_CAN_T_BE_DELETED, ENTITY_NAME, ENTITY_NAME);
	}

	@GetMapping("getNameById/{id}")
	public ResponseEntity<String> getCountryNameById(@PathVariable Long id) {
		log.debug("REST request to get Country Name: {}", id);
		String countryName = countryService.getCountryNameById(id);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.body(countryName);
	}

	@PostMapping("getCountriesByIdList")
	public List<CountryDTO> getCountriesByIdList(@RequestBody Set<Long> idList) {
		log.debug("REST request to get Countries by id list: {}", idList);
		return countryService.getCountriesByIdList(idList);
	}

	@GetMapping("isStateMandatory/{id}")
	public Boolean isStateMandatoryForGivenCountry(@PathVariable Long id) {
		log.debug("REST request to check state is Mandatoy for a given country id: {}", id);
		return countryService.isStateMandatoryForGivenCountry(id);
	}

	@GetMapping("getGstVatType/{id}")
	public ResponseEntity<KeyLabelBeanDTO> getGstVatType(@PathVariable Long id) {
		log.debug("REST request to get gst vat type for given country id: {}", id);
		KeyLabelBeanDTO gstVatType = countryService.getGstVatType(id);
		return ResponseEntity.ok().body(gstVatType);
	}

}
