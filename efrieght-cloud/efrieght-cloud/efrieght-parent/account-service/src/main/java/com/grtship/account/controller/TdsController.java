package com.grtship.account.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.grtship.account.criteria.TdsCriteria;
import com.grtship.account.service.TdsFilterService;
import com.grtship.account.service.TdsService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationReactivationDto;
import com.grtship.core.dto.TdsDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.Tds}.
 */
@RestController
@RequestMapping("/api/tds")
public class TdsController {

	private final Logger log = LoggerFactory.getLogger(TdsController.class);

	private static final String ENTITY_NAME = "accountServiceTds";
	private static final String IF_YOU_WANT_TO_DEACTIVATE_TDS_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX = "If you want to deactivate Tds then, write 'DEACTIVATE' in deactivation Text Box.";
	private static final String DEACTIVATE = "Deactivate";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private TdsService tdsService;

	@Autowired
	private TdsFilterService tdsFilterService;

	/**
	 * {@code POST  /tds} : Create a new tds.
	 *
	 * @param tdsDTO the tdsDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new tdsDTO, or with status {@code 400 (Bad Request)} if the
	 *         tds has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<TdsDTO> createTds(@Valid @RequestBody TdsDTO tdsDTO) throws URISyntaxException {
		log.debug("REST request to save Tds : {}", tdsDTO);
		if (tdsDTO.getId() != null) {
			throw new BadRequestAlertException("A new tds cannot already have an ID", ENTITY_NAME, "idexists");
		}
		TdsDTO result = tdsService.save(tdsDTO);
		return ResponseEntity
				.created(new URI("/api/tds/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /tds} : Updates an existing tds.
	 *
	 * @param tdsDTO the tdsDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated tdsDTO, or with status {@code 400 (Bad Request)} if the
	 *         tdsDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the tdsDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<TdsDTO> updateTds(@Valid @RequestBody TdsDTO tdsDTO) throws URISyntaxException {
		log.debug("REST request to update Tds : {}", tdsDTO);
		if (tdsDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		TdsDTO result = tdsService.save(tdsDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tdsDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /tds} : get all the tds.
	 *
	 * @param pageable the pagination information.
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of tds in body.
	 */
	@GetMapping
	public ResponseEntity<List<TdsDTO>> getAllTds(TdsCriteria criteria, Pageable pageable) {
		log.debug("REST request to get Tds by criteria: {}", criteria);
		Page<TdsDTO> page = tdsFilterService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /tds/count} : count all the tds.
	 *
	 * @param criteria the criteria which the requested entities should match.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
	 *         in body.
	 */
	@GetMapping("count")
	public ResponseEntity<Long> countTds(TdsCriteria criteria) {
		log.debug("REST request to count Tds by criteria: {}", criteria);
		return ResponseEntity.ok().body(tdsFilterService.countByCriteria(criteria));
	}

	/**
	 * {@code GET  /tds/:id} : get the "id" tds.
	 *
	 * @param id the id of the tdsDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the tdsDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<TdsDTO> getTds(@PathVariable Long id) {
		log.debug("REST request to get Tds : {}", id);
		Optional<TdsDTO> tdsDTO = tdsService.findOne(id);
		return ResponseUtil.wrapOrNotFound(tdsDTO);
	}

	/**
	 * {@code DELETE  /tds/:id} : delete the "id" tds.
	 *
	 * @param id the id of the tdsDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteTds(@PathVariable Long id) {
		log.debug("REST request to delete Tds : {}", id);
		tdsService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}

	/**
	 * {@code POST  /deactivate} : deactivate tds.
	 * 
	 * @param deactivationReactivationDto the deactivationReactivationDto to
	 *                                    deactivate tds.
	 *
	 */
	@PostMapping("deactivate")
	public ResponseEntity<TdsDTO> deactivate(@RequestBody DeactivationReactivationDto deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equalsIgnoreCase(DEACTIVATE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					IF_YOU_WANT_TO_DEACTIVATE_TDS_THEN_WRITE_DEACTIVATE_IN_DEACTIVATION_TEXT_BOX);
		TdsDTO result = tdsService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}
}
