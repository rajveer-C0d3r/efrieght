package com.grtship.account.validator;

import com.grtship.core.dto.DeactivationReactivationDto;
import com.grtship.core.dto.TdsDTO;

public interface TdsValidator {

	void saveValidations(TdsDTO tdsDto);

	void deactivateValidations(DeactivationReactivationDto deactivateDto);
}
