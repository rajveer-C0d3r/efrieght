package com.grtship.account.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.grtship.account.criteria.BankReceiptCriteria;
import com.grtship.account.service.BankReceiptService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.dto.BankReceiptDTO;
import com.grtship.core.dto.BankReceiptResponseDTO;
import com.grtship.core.dto.PreviousBankReceiptResponseDTO;

/**
 * REST controller for managing
 * {@link com.grt.efreight.account.domain.BankReceipt}.
 */
@RestController
@RequestMapping("/api/bank-receipts")
public class BankReceiptController {

	private final Logger log = LoggerFactory.getLogger(BankReceiptController.class);

	private static final String ENTITY_NAME = "accountServiceBankReceipt";

	@Value("${spring.application.name}")
	private String applicationName;

	private final BankReceiptService bankReceiptService;

	public BankReceiptController(BankReceiptService bankReceiptService) {
		this.bankReceiptService = bankReceiptService;
	}

	/**
	 * {@code POST  /bank-receipts} : Create a new bankReceipt.
	 *
	 * @param bankReceiptDTO the bankReceiptDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new bankReceiptDTO, or with status {@code 400 (Bad Request)}
	 *         if the bankReceipt has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<BankReceiptDTO> createBankReceipt(@Valid @RequestBody BankReceiptDTO bankReceiptDTO)
			throws URISyntaxException {
		log.debug("REST request to save BankReceipt : {}", bankReceiptDTO);
		if (bankReceiptDTO.getId() != null) {
			throw new BadRequestAlertException("A new bankReceipt cannot already have an ID", ENTITY_NAME, "idexists");
		}
		BankReceiptDTO result = bankReceiptService.save(bankReceiptDTO);
		return ResponseEntity
				.created(new URI("/api/bank-receipts/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /bank-receipts} : get all the bankReceipts.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of bankReceipts in body.
	 */
	@GetMapping
	public ResponseEntity<List<BankReceiptResponseDTO>> getAllBankReceipts(BankReceiptCriteria criteria,
			Pageable pageable) {
		log.debug("REST request to get a page of BankReceipts");
		Page<BankReceiptResponseDTO> page = bankReceiptService.findAll(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /bank-receipts/:id} : get the "id" bankReceipt.
	 *
	 * @param id the id of the bankReceiptDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the bankReceiptDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<BankReceiptResponseDTO> getBankReceipt(@PathVariable Long id) {
		log.debug("REST request to get BankReceipt : {}", id);
		Optional<BankReceiptResponseDTO> bankReceiptDTO = bankReceiptService.findOne(id);
		return ResponseUtil.wrapOrNotFound(bankReceiptDTO);
	}

	@GetMapping("/getPreviousBankReceiptDetails")
	public ResponseEntity<List<PreviousBankReceiptResponseDTO>> getPreviousBankReceiptDetails(
			BankReceiptCriteria criteria) {
		List<PreviousBankReceiptResponseDTO> bankReceiptDetails = bankReceiptService
				.getPreviousBankReceiptDetails(criteria);
		return ResponseEntity.ok().body(bankReceiptDetails);
	}
}
