package com.grtship.client.service;

import javax.validation.Valid;

import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Interface for managing {@link com.grtship.efreight.client.domain.Client}.
 */
public interface ClientService {

    /**
     * Save a client.
     *
     * @param clientDTO the entity to save.
     * @return the persisted entity.
     */
    ClientDTO save(ClientDTO clientDTO);
    
    ClientDTO update(ClientDTO clientDto);

    /**
     * Delete the "id" client.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
	
	ClientDTO deactivate(@Valid DeactivationDTO deactivateDto);

	ClientDTO activate(@Valid ReactivationDTO activateDto);
	
}
