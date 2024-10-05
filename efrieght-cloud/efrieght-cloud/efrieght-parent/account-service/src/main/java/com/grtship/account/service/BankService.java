package com.grtship.account.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.BankDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.account.domain.Bank}.
 */
public interface BankService {

	/**
	 * Save a bank.
	 *
	 * @param bankDTO the entity to save.
	 * @return the persisted entity.
	 */
	BankDTO save(BankDTO bankDTO);

	/**
	 * Get all the banks.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<BankDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" bank.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<BankDTO> findOne(Long id);

	/**
	 * Delete the "id" bank.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);
}
