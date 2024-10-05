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
import com.grtship.account.criteria.LedgerCriteria;
import com.grtship.account.service.LedgerQueryService;
import com.grtship.account.service.LedgerService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.constant.AuthoritiesConstants;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.LedgerCreationDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.Ledger}.
 */
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

	private static final String IF_YOU_WANT_TO_REACTIVE_LEDGER_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX = "If you want to reactive Ledger then you need to type 'REACTIVATE' in text box.";

	private static final String TYPE = "type";

	private static final String IF_YOU_WANT_TO_DEACTIVATE_LEDGER_THEN_TYPE_DEACTIVATE_IN_THE_TEXT_BOX = "If you want to deactivate Ledger then type 'DEACTIVATE' in the text Box";

	private static final String DEACTIVATE = "DEACTIVATE";

	private static final String IDEXISTS = "idexists";

	private static final String A_NEW_LEDGER_CANNOT_ALREADY_HAVE_AN_ID = "A new ledger cannot already have an ID";

	private static final String IDNULL = "idnull";

	private static final String INVALID_ID = "Invalid id";

	private static final String NOT_DELETABLE = "notDeletable";

	private static final String LEDGER_CAN_T_BE_DELETE_PLEASE_CONTACT_ADMIN = "Ledger can't be delete. Please contact Admin.";

	private final Logger log = LoggerFactory.getLogger(LedgerController.class);

	private static final String ENTITY_NAME = "accountServiceLedger";

	@Value("${spring.application.name}")
	private String applicationName;

	private final LedgerService ledgerService;

	@Autowired
	private LedgerQueryService ledgerQueryService;

	public LedgerController(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}

	/**
	 * {@code POST  /ledgers} : Create a new ledger.
	 *
	 * @param ledgerDTO the ledgerDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new ledgerDTO, or with status {@code 400 (Bad Request)} if
	 *         the ledger has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_ADD + "\")")
	public ResponseEntity<LedgerDTO> createLedger(@Valid @RequestBody LedgerCreationDTO ledgerDto)
			throws URISyntaxException {
		log.debug("REST request to save Ledger : {}", ledgerDto);
		if (ledgerDto.getId() != null) {
			throw new BadRequestAlertException(A_NEW_LEDGER_CANNOT_ALREADY_HAVE_AN_ID, ENTITY_NAME, IDEXISTS);
		}
		LedgerDTO result = ledgerService.save(ledgerDto);
		return ResponseEntity
				.created(new URI("/api/ledgers/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /ledgers} : Updates an existing ledger.
	 *
	 * @param ledgerDto the ledgerDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated ledgerDTO, or with status {@code 400 (Bad Request)} if
	 *         the ledgerDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the ledgerDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_UPDATE + "\")")
	public ResponseEntity<LedgerDTO> updateLedger(@Valid @RequestBody LedgerDTO ledgerDto) throws URISyntaxException {
		log.debug("REST request to update Ledger : {}", ledgerDto);
		if (ledgerDto.getId() == null) {
			throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, IDNULL);
		}
		LedgerDTO result = ledgerService.update(ledgerDto);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ledgerDto.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /ledgers} : get all the ledgers.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of ledgers in body.
	 */
	@GetMapping
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_VIEW + "\")")
	public ResponseEntity<List<LedgerDTO>> getAllLedgers(LedgerCriteria ledgerCriteria, Pageable pageable) {
		log.debug("REST request to get a page of Ledgers");
		Page<LedgerDTO> page = ledgerQueryService.findByCriteria(ledgerCriteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /ledgers/:id} : get the "id" ledger.
	 *
	 * @param id the id of the ledgerDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the ledgerDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_VIEW + "\")")
	public ResponseEntity<LedgerDTO> getLedger(@PathVariable Long id) {
		log.debug("REST request to get Ledger : {}", id);
		Optional<LedgerDTO> ledgerDTO = ledgerQueryService.findOne(id);
		return ResponseUtil.wrapOrNotFound(ledgerDTO);
	}

	/**
	 * {@code DELETE  /ledgers/:id} : delete the "id" ledger.
	 *
	 * @param id the id of the ledgerDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_DELETE + "\")")
	public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
		throw new BadRequestAlertException(LEDGER_CAN_T_BE_DELETE_PLEASE_CONTACT_ADMIN, ENTITY_NAME, NOT_DELETABLE);
	}

	/**
	 * {@code POST  /deactivateLedger} : deactivate ledger.
	 * 
	 * @param deactivationReactivationDto the deactivationReactivationDto to
	 *                                    deactivate ledger.
	 *
	 */
	@PostMapping("/deactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_DEACTIVATE + "\")")
	public ResponseEntity<LedgerDTO> deactivate(@RequestBody DeactivationDTO deactivateDto) {
		if (StringUtils.isEmpty(deactivateDto.getType()) || !deactivateDto.getType().equals(DEACTIVATE))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_DEACTIVATE_LEDGER_THEN_TYPE_DEACTIVATE_IN_THE_TEXT_BOX,
					ENTITY_NAME, TYPE);
		LedgerDTO result = ledgerService.deactivate(deactivateDto);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/reactivate")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.LEDGER_REACTIVATE + "\")")
	public ResponseEntity<LedgerDTO> reactivateBranch(@Valid @RequestBody ReactivationDTO reactivateDto) {
		if (StringUtils.isEmpty(reactivateDto.getType()) || (!reactivateDto.getType().equals("REACTIVATE")))
			throw new BadRequestAlertException(
					IF_YOU_WANT_TO_REACTIVE_LEDGER_THEN_YOU_NEED_TO_TYPE_REACTIVATE_IN_TEXT_BOX, ENTITY_NAME, TYPE);
		LedgerDTO result = ledgerService.reactivate(reactivateDto);
		return ResponseEntity.ok().body(result);
	}

}
