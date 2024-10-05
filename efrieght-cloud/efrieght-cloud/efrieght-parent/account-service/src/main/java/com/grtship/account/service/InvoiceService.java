package com.grtship.account.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.InvoiceDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.Invoice}.
 */
public interface InvoiceService {

	/**
	 * Save a invoice.
	 *
	 * @param invoiceDTO the entity to save.
	 * @return the persisted entity.
	 */
	InvoiceDTO save(InvoiceDTO invoiceDTO);

	/**
	 * Get all the invoices.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<InvoiceDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" invoice.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<InvoiceDTO> findOne(Long id);

	/**
	 * Delete the "id" invoice.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	Map<Long, InvoiceDTO> getInvoiceMap(Collection<Long> ids);

	void saveAll(List<InvoiceDTO> invoiceDtos);
}
