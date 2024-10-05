package com.grtship.mdm.validator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CurrencyDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Currency;
import com.grtship.mdm.mapper.CurrencyMapper;
import com.grtship.mdm.repository.CurrencyRepository;

@Component
public class CurrencyValidator implements Validator<CurrencyDTO>{

	private static final String CURRENCY_NAME_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_NAME = "Currency Name Already Exists, Enter Another Currency Name";

	private static final String CURRENCY_CODE_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_CODE = "Currency Code Already Exists, Enter Another Currency Code";

	@Autowired
	private CurrencyRepository currencyRepository;
	
	@Autowired
	private CurrencyMapper currencyMapper;

	@Override
	public List<ValidationError> validate(CurrencyDTO currencyDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			saveValidations(currencyMapper.toEntity(currencyDTO),errors);
		}
		return errors;
	}
	
	private void saveValidations(Currency currency,List<ValidationError> errors) {
		addNonEmpty(errors,validateCurrencyCode(currency));
		addNonEmpty(errors,validateCurrencyName(currency));
	}
	
	private ValidationError validateCurrencyCode(Currency currency) {
		if (currency.getId() == null) {
			if (currencyRepository.findByCode(currency.getCode()) != null) {
				return returnCodeValidationError(currency);
			}
		} else {
			if (currencyRepository.findyByCodeAndId(currency.getCode(), currency.getId()) != null) {
				return returnCodeValidationError(currency);
			}
		}
		return null;
	}

	private ValidationError returnCodeValidationError(Currency currency) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(CURRENCY_CODE_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_CODE)
				.referenceId(currency.getId()==null?"":String.valueOf(currency.getId()))
				.build();
	}

	private ValidationError validateCurrencyName(Currency currency) {
		if (currency.getId() == null) {
			if (currencyRepository.findByName(currency.getName()) != null) {
				return returnNameValidationError(currency);
			}
		} else {
			if (currencyRepository.findyByNameAndId(currency.getName(), currency.getId()) != null) {
				return returnNameValidationError(currency);
			}
		}
		return null;
	}

	private ValidationError returnNameValidationError(Currency currency) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(CURRENCY_NAME_ALREADY_EXISTS_ENTER_ANOTHER_CURRENCY_NAME)
				.referenceId(currency.getId()==null?"":String.valueOf(currency.getId()))
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
