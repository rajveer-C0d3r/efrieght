package com.grtship.account.validator.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.account.repository.BankRepository;
import com.grtship.account.validator.BankValidator;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BankDTO;
import com.grtship.core.enumeration.ValidationErrorType;

import io.micrometer.core.instrument.util.StringUtils;

@Component
public class BankValidatorImpl implements BankValidator,Validator<BankDTO> {

	private static final String BRANCH_LOCATION_IS_MANDATORY = "Branch Location is mandatory.";
	private static final String BRANCH_CITY_IS_MANDATORY = "Branch city is mandatory.";
	private static final String BRANCH_ADDRESS_IS_MANDATORY = "Branch Address is mandatory.";
	private static final String ACCOUNT_NO_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_ACCOUNT_NO = "Account No already exists, Please Enter another Account No";
	private static final String BANK_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_BANK_CODE = "Bank Code already exists, Please Enter another Bank Code";

	@Autowired
	private BankRepository bankRepository;
	
	@Override
	public List<ValidationError> validate(BankDTO bankDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			saveValidations(bankDTO,errors);
		}
		return errors;
	}

	@Override
	public void saveValidations(BankDTO bankDto,List<ValidationError> errors) {
		addNonEmpty(errors,bankCodeValidations(bankDto));
		addNonEmpty(errors,accountNoValidatons(bankDto));
		addressValidations(bankDto,errors);
	}

	private ValidationError addressValidations(BankDTO bankDto,List<ValidationError> errors) {
		if (bankDto.getAddress() != null) {
			if (StringUtils.isBlank(bankDto.getAddress().getAddress()))
				 addNonEmpty(errors,returnAddressEmptyValidationError(bankDto.getId()));
			addNonEmpty(errors,cityValidations(bankDto.getAddress()));
			addNonEmpty(errors,locationValidations(bankDto.getAddress()));
		}
		return null;
	}
	
	private ValidationError returnAddressEmptyValidationError(Long id) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(BRANCH_ADDRESS_IS_MANDATORY)
				.referenceId(id==null?"":String.valueOf(id))
				.build();
	}

	private ValidationError cityValidations(AddressDTO address) {
		if (address.getCityId() == null)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(BRANCH_CITY_IS_MANDATORY)
					.build();
		return null;
	}

	private ValidationError locationValidations(AddressDTO address) {
		if (StringUtils.isBlank(address.getLocation()))
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(BRANCH_LOCATION_IS_MANDATORY)
					.build();
		return null;
	}

	private ValidationError bankCodeValidations(BankDTO bankDto) {
		if (bankDto.getId() == null && bankDto.getCode() != null && !CollectionUtils
				.isEmpty(bankRepository.findByCodeAndCompanyId(bankDto.getCode(), bankDto.getCompanyId()))) {
			return returnCodeValidationError(bankDto);
		} else if (bankDto.getId() != null && bankDto.getCode() != null && bankDto.getCompanyId() != null
				&& !CollectionUtils.isEmpty(bankRepository.findByCodeAndCompanyIdAndIdNot(bankDto.getCode(),
						bankDto.getId(), bankDto.getCompanyId()))) {
			return returnCodeValidationError(bankDto);
		}
		return null;
	}

	private ValidationError returnCodeValidationError(BankDTO bankDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(BANK_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_BANK_CODE)
				.referenceId(bankDto.getId()==null?"":String.valueOf(bankDto.getId()))
				.build();
	}
	

	private ValidationError accountNoValidatons(BankDTO bankDto) {
		if (bankDto.getId() == null && bankDto.getAccountNo() != null && !CollectionUtils
				.isEmpty(bankRepository.findByAccountNoAndCompanyId(bankDto.getAccountNo(), bankDto.getCompanyId()))) {
			return returnAccountNoValidationError(bankDto);
		} else if (bankDto.getId() != null && bankDto.getAccountNo() != null && bankDto.getCompanyId() != null
				&& !CollectionUtils.isEmpty(bankRepository.findByAccountNoAndCompanyIdAndIdNot(bankDto.getAccountNo(),
						bankDto.getId(), bankDto.getCompanyId()))) {
			return returnAccountNoValidationError(bankDto);
		}
		return null;
	}

	private ValidationError returnAccountNoValidationError(BankDTO bankDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(ACCOUNT_NO_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_ACCOUNT_NO)
				.referenceId(bankDto.getId()==null?"":String.valueOf(bankDto.getId()))
				.build();
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
