package com.grtship.account.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.account.criteria.BankReceiptCriteria;
import com.grtship.core.dto.BankReceiptDTO;
import com.grtship.core.dto.BankReceiptResponseDTO;
import com.grtship.core.dto.PreviousBankReceiptResponseDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.BankReceipt}.
 */
public interface BankReceiptService {

	/**
	 * Save a bankReceipt.
	 *
	 * @param bankReceiptDTO the entity to save.
	 * @return the persisted entity.
	 */
	BankReceiptDTO save(BankReceiptDTO bankReceiptDTO);

	/**
	 * Get all the bankReceipts.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<BankReceiptResponseDTO> findAll(BankReceiptCriteria criteria, Pageable pageable);

	/**
	 * Get the "id" bankReceipt.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<BankReceiptResponseDTO> findOne(Long id);

	List<PreviousBankReceiptResponseDTO> getPreviousBankReceiptDetails(BankReceiptCriteria criteria);
}
