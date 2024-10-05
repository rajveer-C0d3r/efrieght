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

import com.grtship.account.service.InvoiceTransactionService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.InvoiceTransactionDTO;

/**
 * REST controller for managing
 * {@link com.grt.efreight.account.domain.InvoiceTransaction}.
 */
@RestController
@RequestMapping("/api")
public class InvoiceTransactionController {

	private final Logger log = LoggerFactory.getLogger(InvoiceTransactionController.class);

	private static final String ENTITY_NAME = "accountServiceInvoiceTransaction";

	@Value("${spring.application.name}")
	private String applicationName;

	private final InvoiceTransactionService invoiceTransactionService;

	public InvoiceTransactionController(InvoiceTransactionService invoiceTransactionService) {
		this.invoiceTransactionService = invoiceTransactionService;
	}

	/**
	 * {@code POST  /invoice-transactions} : Create a new invoiceTransaction.
	 *
	 * @param invoiceTransactionDTO the invoiceTransactionDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new invoiceTransactionDTO, or with status
	 *         {@code 400 (Bad Request)} if the invoiceTransaction has already an
	 *         ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/invoice-transactions")
	public ResponseEntity<InvoiceTransactionDTO> createInvoiceTransaction(
			@Valid @RequestBody InvoiceTransactionDTO invoiceTransactionDTO) throws URISyntaxException {
		log.debug("REST request to save InvoiceTransaction : {}", invoiceTransactionDTO);
		if (invoiceTransactionDTO.getId() != null) {
			throw new BadRequestAlertException("A new invoiceTransaction cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		InvoiceTransactionDTO result = invoiceTransactionService.save(invoiceTransactionDTO);
		return ResponseEntity
				.created(new URI("/api/invoice-transactions/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /invoice-transactions} : Updates an existing invoiceTransaction.
	 *
	 * @param invoiceTransactionDTO the invoiceTransactionDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated invoiceTransactionDTO, or with status
	 *         {@code 400 (Bad Request)} if the invoiceTransactionDTO is not valid,
	 *         or with status {@code 500 (Internal Server Error)} if the
	 *         invoiceTransactionDTO couldn't be updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/invoice-transactions")
	public ResponseEntity<InvoiceTransactionDTO> updateInvoiceTransaction(
			@Valid @RequestBody InvoiceTransactionDTO invoiceTransactionDTO) throws URISyntaxException {
		log.debug("REST request to update InvoiceTransaction : {}", invoiceTransactionDTO);
		if (invoiceTransactionDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		InvoiceTransactionDTO result = invoiceTransactionService.save(invoiceTransactionDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME,
				invoiceTransactionDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /invoice-transactions} : get all the invoiceTransactions.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of invoiceTransactions in body.
	 */
	@GetMapping("/invoice-transactions")
	public List<InvoiceTransactionDTO> getAllInvoiceTransactions() {
		log.debug("REST request to get all InvoiceTransactions");
		return invoiceTransactionService.findAll();
	}

	/**
	 * {@code GET  /invoice-transactions/:id} : get the "id" invoiceTransaction.
	 *
	 * @param id the id of the invoiceTransactionDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the invoiceTransactionDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/invoice-transactions/{id}")
	public ResponseEntity<InvoiceTransactionDTO> getInvoiceTransaction(@PathVariable Long id) {
		log.debug("REST request to get InvoiceTransaction : {}", id);
		Optional<InvoiceTransactionDTO> invoiceTransactionDTO = invoiceTransactionService.findOne(id);
		return ResponseUtil.wrapOrNotFound(invoiceTransactionDTO);
	}

	/**
	 * {@code DELETE  /invoice-transactions/:id} : delete the "id"
	 * invoiceTransaction.
	 *
	 * @param id the id of the invoiceTransactionDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("/invoice-transactions/{id}")
	public ResponseEntity<Void> deleteInvoiceTransaction(@PathVariable Long id) {
		log.debug("REST request to delete InvoiceTransaction : {}", id);
		invoiceTransactionService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}
}
