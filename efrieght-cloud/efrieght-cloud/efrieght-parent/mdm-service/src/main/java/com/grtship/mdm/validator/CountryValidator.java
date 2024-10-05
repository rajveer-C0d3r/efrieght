package com.grtship.mdm.validator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.StateDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Country;
import com.grtship.mdm.repository.CountryRepository;
import com.grtship.mdm.service.ObjectAliasService;

@Component
public class CountryValidator implements Validator<CountryDTO> {

	private static final String STATES_ARE_MANDATORY_PLEASE_ENTER_STATE_DETAILS = "States are mandatory. Please enter state details.";
	private static final String COUNTRY_NAME_CAN_T_BE_DUPLICATE = "Country Name can't be duplicate!";
	private static final String COUNTRY_CODE_CAN_T_BE_DUPLICATE = "Country Code can't be duplicate!";
	private static final String STATE_CODE_CAN_T_BE_DUPLICATE = "State code can't be duplicate!";
	private static final String STATE_NAME_CAN_T_BE_DUPLICATE = "State Name can't be duplicate!";
	private static final String DOCUMENT_NAME_CAN_T_BE_DUPLICATE = "Document Name can't be duplicate!";
	private static final String YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE = "You Entered Duplicate Aliases, Aliases Must Be Unique";
	private static final String COUNTRY = "Country";
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private ObjectAliasService aliasService;
	
	@Override
	public List<ValidationError> validate(CountryDTO countryDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if (action.equals("save") || action.equals("update")) {
			saveValidation(countryDTO,errors);
		}
		return errors;
	}

	public void saveValidation(CountryDTO countryDto,List<ValidationError> errors) {
		addNonEmpty(errors,validateAlias(countryDto));
		addNonEmpty(errors,validateCode(countryDto));
		addNonEmpty(errors,validateName(countryDto));
		validateStates(countryDto,errors);
		addNonEmpty(errors,validateDocuments(countryDto));
	}

	private ValidationError validateCode(CountryDTO countryDto) {
		if (countryDto.getCode() != null) {
			List<Country> countries = countryRepository.findByCodeAndClientIdAndCompanyId(countryDto.getCode(),
					countryDto.getClientId(), countryDto.getCompanyId());
			Boolean countryPresent = countries.stream().filter(obj -> obj.getId() != null && countryDto.getId() != null
					&& !(obj.getId().equals(countryDto.getId()))).map(obj -> obj).findAny().isPresent();
			if ((countryDto.getId() == null && !CollectionUtils.isEmpty(countries))
					|| countryPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(COUNTRY_CODE_CAN_T_BE_DUPLICATE)
						.referenceId(countryDto.getId()==null?"":String.valueOf(countryDto.getId()))
						.build();
			}
		}
		return null;

	}

	private ValidationError validateName(CountryDTO countryDto) {
		if (countryDto.getName() != null) {
			List<Country> countries = countryRepository.findByNameAndClientIdAndCompanyId(countryDto.getName(),
					countryDto.getClientId(), countryDto.getCompanyId());
			Boolean countryPresent = countries.stream().filter(obj -> obj.getId() != null && countryDto.getId() != null
					&& !(obj.getId().equals(countryDto.getId()))).map(obj -> obj).findAny().isPresent();
			if ((countryDto.getId() == null && !CollectionUtils.isEmpty(countries))
					|| countryPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(COUNTRY_NAME_CAN_T_BE_DUPLICATE)
						.referenceId(countryDto.getId()==null?"":String.valueOf(countryDto.getId()))
						.build();
			}
		}
		return null;

	}

	private void validateStates(CountryDTO countryDto,List<ValidationError> errors) {
		if (countryDto.getIsStateMandatory().equals(Boolean.TRUE) && CollectionUtils.isEmpty(countryDto.getStates()))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,STATES_ARE_MANDATORY_PLEASE_ENTER_STATE_DETAILS);
		if (!CollectionUtils.isEmpty(countryDto.getStates())) {
			addNonEmpty(errors,validateStateCode(countryDto.getStates()));
			addNonEmpty(errors,validateStateName(countryDto.getStates()));
		}
	}

	private ValidationError validateStateCode(Set<StateDTO> states) {
		List<String> codeList = states.stream().filter(state -> state.getCode() != null).map(StateDTO::getCode)
				.collect(Collectors.toList());
		Set<String> codeSet = states.stream().filter(state -> state.getCode() != null).map(StateDTO::getCode)
				.collect(Collectors.toSet());
		if (codeList.size() != codeSet.size())
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(STATE_CODE_CAN_T_BE_DUPLICATE)
					.build();
		return null;
	}

	private ValidationError validateStateName(Set<StateDTO> states) {
		List<String> nameList = states.stream().filter(state -> state.getName() != null).map(StateDTO::getName)
				.collect(Collectors.toList());
		Set<String> nameSet = states.stream().filter(state -> state.getName() != null).map(StateDTO::getName)
				.collect(Collectors.toSet());
		if (nameList.size() != nameSet.size())
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(STATE_NAME_CAN_T_BE_DUPLICATE)
					.build();
		return null;
	}

	private ValidationError validateDocuments(CountryDTO countryDto) {
		if (!CollectionUtils.isEmpty(countryDto.getDocuments())) {
			List<String> nameList = countryDto.getDocuments().stream().filter(document -> document.getName() != null)
					.map(DocumentDTO::getName).collect(Collectors.toList());
			Set<String> nameSet = countryDto.getDocuments().stream().filter(document -> document.getName() != null)
					.map(DocumentDTO::getName).collect(Collectors.toSet());
			if (nameList.size() != nameSet.size())
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(DOCUMENT_NAME_CAN_T_BE_DUPLICATE)
						.referenceId(countryDto.getId()==null?"":String.valueOf(countryDto.getId()))
						.build();
				
		}
		return null;

	}
	
	private ValidationError validateAlias(CountryDTO countryDto) {
		if (!CollectionUtils.isEmpty(countryDto.getAliases())) {
			List<String> aliasesToSave = countryDto.getAliases().stream()
					.filter(objectAliasDTO -> objectAliasDTO.getName() != null).map(ObjectAliasDTO::getName)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(aliasesToSave)) {
				List<String> duplicateAliases = aliasesToSave.stream()
						.filter(name -> Collections.frequency(aliasesToSave, name) > 1).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateAliases)) {
					return ObjectValidationError.builder()
							.type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE)
							.referenceId(countryDto.getId()==null?"":String.valueOf(countryDto.getId()))
							.build();
				}
			}
			
		}
		

		if (countryDto.getId() == null) {
			Set<String> sets=countryDto.getAliases().stream()
					 .map(alias -> alias.getName()).collect(Collectors.toSet());
					return aliasService.checkForDuplicateAlias(sets, COUNTRY);
		} else {
			return aliasService.checkForDuplicateAlias(countryDto.getAliases(), COUNTRY,countryDto.getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
