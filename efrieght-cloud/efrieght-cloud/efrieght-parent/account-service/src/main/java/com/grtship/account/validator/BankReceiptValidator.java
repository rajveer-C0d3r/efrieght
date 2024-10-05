package com.grtship.account.validator;

import com.grtship.core.dto.BankReceiptDTO;

public interface BankReceiptValidator {

	void saveValidations(BankReceiptDTO bankReceiptDto);
}
