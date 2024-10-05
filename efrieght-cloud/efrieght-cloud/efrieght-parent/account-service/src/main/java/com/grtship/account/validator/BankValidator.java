package com.grtship.account.validator;

import java.util.List;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.dto.BankDTO;

public interface BankValidator {

	void saveValidations(BankDTO bankDto,List<ValidationError> errors);
}
