package com.grtship.client.service;

import java.util.List;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;

/**
 * Service Interface for managing {@link com.grtship.efreight.client.domain.Client} validations.
 */
public interface ClientValidatorService {

	void saveValidations(ClientDTO clientDto,List<ValidationError> errors);
	
	void updateValidations(ClientDTO clientDto,List<ValidationError> errors);
	
	void deactivateValidations(DeactivationDTO deactivateDto,List<ValidationError> errors);
	
	void activateValidations(ReactivationDTO activateDto,List<ValidationError> errors);
}
