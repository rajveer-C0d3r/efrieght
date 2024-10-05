package com.grtship.account.service;

import java.util.Optional;

import com.grtship.core.dto.DeactivationReactivationDto;
import com.grtship.core.dto.TdsDTO;

/**
 * Service Interface for managing {@link com.grt.efreight.account.domain.Tds}.
 */
public interface TdsService {

	/**
	 * Save a tds.
	 *
	 * @param tdsDTO the entity to save.
	 * @return the persisted entity.
	 */
	TdsDTO save(TdsDTO tdsDTO);

	/**
	 * Get the "id" tds.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<TdsDTO> findOne(Long id);

	/**
	 * Delete the "id" tds.
	 *
	 * @param id the id of the entity.
	 */
	void delete(Long id);

	TdsDTO deactivate(DeactivationReactivationDto deactivateDto);
}
