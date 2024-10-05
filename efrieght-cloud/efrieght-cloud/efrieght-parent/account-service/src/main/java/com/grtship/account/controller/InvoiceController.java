package com.grtship.account.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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

import com.grtship.account.criteria.InvoiceCriteria;
import com.grtship.account.service.InvoiceFilterService;
import com.grtship.account.service.InvoiceService;
import com.grtship.account.util.HeaderUtil;
import com.grtship.account.util.PaginationUtil;
import com.grtship.account.util.ResponseUtil;
import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.core.annotation.Auditable;
import com.grtship.core.dto.InvoiceDTO;
import com.grtship.core.dto.PageableEntityDTO;

/**
 * REST controller for managing {@link com.grt.efreight.account.domain.Invoice}.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

	private final Logger log = LoggerFactory.getLogger(InvoiceController.class);

	private static final String ENTITY_NAME = "accountServiceInvoice";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceFilterService invoiceFilterService;

	/**
	 * {@code POST  /invoices} : Create a new invoice.
	 *
	 * @param invoiceDTO the invoiceDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new invoiceDTO, or with status {@code 400 (Bad Request)} if
	 *         the invoice has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping
	public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) throws URISyntaxException {
		log.debug("REST request to save Invoice : {}", invoiceDTO);
		if (invoiceDTO.getId() != null) {
			throw new BadRequestAlertException("A new invoice cannot already have an ID", ENTITY_NAME, "idexists");
		}
		InvoiceDTO result = invoiceService.save(invoiceDTO);
		return ResponseEntity
				.created(new URI("/api/invoices/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /invoices} : Updates an existing invoice.
	 *
	 * @param invoiceDTO the invoiceDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated invoiceDTO, or with status {@code 400 (Bad Request)} if
	 *         the invoiceDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the invoiceDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping
	public ResponseEntity<InvoiceDTO> updateInvoice(@RequestBody InvoiceDTO invoiceDTO) throws URISyntaxException {
		log.debug("REST request to update Invoice : {}", invoiceDTO);
		if (invoiceDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		InvoiceDTO result = invoiceService.save(invoiceDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, invoiceDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /invoices} : get all the invoices.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of invoices in body.
	 */
	@GetMapping
	public ResponseEntity<PageableEntityDTO<InvoiceDTO>> getAllInvoices(InvoiceCriteria criteria, Pageable pageable) {
		log.debug("REST request to get a page of Invoices");
		Page<InvoiceDTO> page = invoiceFilterService.findByCriteria(criteria, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers)
				.body(new PageableEntityDTO<InvoiceDTO>(page.getContent(), page.getTotalElements()));
	}

	/**
	 * {@code GET  /invoices/:id} : get the "id" invoice.
	 *
	 * @param id the id of the invoiceDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the invoiceDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("{id}")
	public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long id) {
		log.debug("REST request to get Invoice : {}", id);
		Optional<InvoiceDTO> invoiceDTO = invoiceService.findOne(id);
		return ResponseUtil.wrapOrNotFound(invoiceDTO);
	}

	/**
	 * {@code DELETE  /invoices/:id} : delete the "id" invoice.
	 *
	 * @param id the id of the invoiceDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@Auditable
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
		log.debug("REST request to delete Invoice : {}", id);
		invoiceService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}
}
