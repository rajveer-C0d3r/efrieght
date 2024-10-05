package com.grtship.mdm.service;

import javax.validation.Valid;

import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ExternalEntityDTO;
import com.grtship.core.dto.ExternalEntityRequestDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Interface for managing
 * {@link com.grt.efreight.domain.ExternalEntity}.
 */
public interface ExternalEntityService {

	/**
	 * Save a externalEntity.
	 *
	 * @param externalEntityDTO the entity to save.
	 * @return the persisted entity.
	 */
	ExternalEntityDTO save(ExternalEntityRequestDTO entityDto);

	ExternalEntityDTO update(ExternalEntityDTO entityDto);

	ExternalEntityDTO deactivate(@Valid DeactivationDTO deactivateDto);
	
	ExternalEntityDTO reactivate(@Valid ReactivationDTO reactivateDto);
}
