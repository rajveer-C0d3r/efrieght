package com.grtship.account.service;

import javax.validation.Valid;

import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.LedgerCreationDTO;
import com.grtship.core.dto.LedgerDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.account.domain.Ledger}.
 */
public interface LedgerService {

	/**
	 * Save a ledger.
	 *
	 * @param ledgerDTO the entity to save.
	 * @return the persisted entity.
	 */
	LedgerDTO save(LedgerCreationDTO ledgerDto);

	LedgerDTO deactivate(DeactivationDTO deactivateDto);

	LedgerDTO update(@Valid LedgerDTO ledgerDto);

	LedgerDTO reactivate(@Valid ReactivationDTO reactivateDto);

}
