package com.grtship.mdm.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grtship.core.dto.CurrencyDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.domain.Currency}.
 */
public interface CurrencyService {

	/**
	 * Save a currency.
	 *
	 * @param currencyDTO the entity to save.
	 * @return the persisted entity.
	 */
	CurrencyDTO save(CurrencyDTO currencyDTO);

	/**
	 * Get all the currencies.
	 *
	 * @param pageable the pagination information.
	 * @return the list of entities.
	 */
	Page<CurrencyDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" currency.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<CurrencyDTO> findOne(Long id);

	/**
	 * Delete the "id" currency.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	Map<Long, String> findCurrencyNameByIdList(Set<Long> idList);

	Map<Long, String> getCurrencyIdNameByIds(List<Long> currencyIdList);

	Map<Long, CurrencyDTO> getAllCurrenciesIdList(List<Long> currencyIdList);
}
