package com.grtship.account.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.grtship.core.dto.InvoiceTransactionDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.InvoiceTransaction}.
 */
public interface InvoiceTransactionService {

	/**
	 * Save a invoiceTransaction.
	 *
	 * @param invoiceTransactionDTO the entity to save.
	 * @return the persisted entity.
	 */
	InvoiceTransactionDTO save(InvoiceTransactionDTO invoiceTransactionDTO);

	void saveAll(Set<InvoiceTransactionDTO> dtos);

	/**
	 * Get all the invoiceTransactions.
	 *
	 * @return the list of entities.
	 */
	List<InvoiceTransactionDTO> findAll();

	/**
	 * Get the "id" invoiceTransaction.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<InvoiceTransactionDTO> findOne(Long id);

	/**
	 * Delete the "id" invoiceTransaction.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
