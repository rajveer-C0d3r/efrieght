package com.grtship.mdm.validator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DestinationType;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.repository.DestinationRepository;
import com.grtship.mdm.service.CountryService;
import com.grtship.mdm.service.ObjectAliasService;

@Component
public class DestinationValidator implements Validator<DestinationDTO> {

	private static final String DESTINATION_CODE_ALREADY_EXISTS_ENTER_ANOTHER_DESTINATION_CODE = "Destination Code Already Exists, Enter Another Destination Code";

	private static final String DESTINATION_NAME_ALREADY_EXISTS_ENTER_ANOTHER_DESTINATION_NAME = "Destination Name Already Exists, Enter Another Destination Name";


	private static final String DESTINATION = "Destination";

	private static final String CITY_IS_MANDATORY_PLEASE_SELECT_CITY = "City is Mandatory, Please Select City.";

	private static final String IF_DESTINATION_TYPE_IS_AIRPORT_THEN_IATA_AIRPORT_CODE_IS_MANDATORY = "If Destination Type Is Airport, Then IATA Airport Code Is Mandatory.";

	private static final String COUNTRY_IS_MANDATORY_PLEASE_SELECT_COUNTRY = "Country Is Mandatory, Please Select Country.";

	private static final String IF_DESTINATION_TYPE_IS_TERMINAL_THEN_PORT_IS_MANDATORY = "If Destination Type is Terminal, Then Port is Mandatory";

	private static final String FOR_A_GIVEN_COUNTY_STATE_IS_MANDATORY_PLEASE_ENTER_STATE = "For A Given County, State Is Mandatory, Please Enter State";

	private static final String YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE = "You Entered Duplicate Aliases, Aliases Must Be Unique.";
	
	private static final String COMPANY_IS_REQUIRED = "Company is required";

	@Autowired
	private DestinationRepository destinationRepository;

	@Autowired
	private CountryService countryService;

	@Autowired
	private ObjectAliasService aliasService;
	
	@Override
	public List<ValidationError> validate(DestinationDTO destinationDto, String action) {
		List<ValidationError> errors = new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			if(ObjectUtils.isEmpty(destinationDto.getIsAdminCreated())) {
				addNonEmpty(errors,destinationCompanyValidations(destinationDto));	
			}
			addNonEmpty(errors,destinationCodeValidations(destinationDto));
			addNonEmpty(errors,destinationNameValidations(destinationDto));
			addNonEmpty(errors,destinationStateValidations(destinationDto));
			addNonEmpty(errors,destinationAliasValidations(destinationDto));
			addNonEmpty(errors,destinationPortValidations(destinationDto));
			addNonEmpty(errors,destinationCityValidations(destinationDto));
			addNonEmpty(errors,destinationCountryValidations(destinationDto));
			addNonEmpty(errors,destinationIATAAirportCodeValidations(destinationDto));	
		}
		return errors;
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
	
	private ValidationError destinationCodeValidations(DestinationDTO destinationDto) {
		if (destinationDto.getId() == null) {
			if (destinationRepository.findByCodeAndClientIdAndCompanyId(destinationDto.getCode(),
					destinationDto.getClientId(), destinationDto.getCompanyId()) != null) {
				return returnCodeValidationError(destinationDto);
			}

		} else {
			if (destinationRepository.findByCodeAndIdNotAndClientIdAndCompanyId(destinationDto.getCode(),
					destinationDto.getId(), destinationDto.getClientId(), destinationDto.getCompanyId()) != null) {
				return returnCodeValidationError(destinationDto);
			}
		}
		return null;
	}


	private ValidationError returnCodeValidationError(DestinationDTO destinationDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(DESTINATION_CODE_ALREADY_EXISTS_ENTER_ANOTHER_DESTINATION_CODE)
				.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
				.build();
	}

	private ValidationError destinationNameValidations(DestinationDTO destinationDto) {
		if (destinationDto.getId() == null) {
			if (destinationRepository.findByNameAndCountryIdAndClientIdAndCompanyId(destinationDto.getName(),
					destinationDto.getCountryId(), destinationDto.getClientId(),
					destinationDto.getCompanyId()) != null) {
				return returnNameValidationError(destinationDto);
			}
		} else {
			if (destinationRepository.findByNameAndIdNotAndCountryIdAndClientIdAndCompanyId(destinationDto.getName(),
					destinationDto.getId(), destinationDto.getCountryId(), destinationDto.getClientId(),
					destinationDto.getCompanyId()) != null) {
				return returnNameValidationError(destinationDto);
			}
		}
		return null;
	}
	
	private ValidationError returnNameValidationError(DestinationDTO destinationDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(DESTINATION_NAME_ALREADY_EXISTS_ENTER_ANOTHER_DESTINATION_NAME)
				.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
				.build();
	}

	private ValidationError destinationAliasValidations(DestinationDTO destinationDto) {
		if (!CollectionUtils.isEmpty(destinationDto.getAliases())) {

			List<String> aliasesToSave = destinationDto.getAliases().stream()
					.filter(objectAliasDto -> objectAliasDto.getName() != null).map(ObjectAliasDTO::getName)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(aliasesToSave)) {
				List<String> duplicateAliases = aliasesToSave.stream()
						.filter(name -> Collections.frequency(aliasesToSave, name) > 1).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateAliases)) {
					return ObjectValidationError.builder()
							.type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE)
							.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
							.build();
				}
			}
			
			if (destinationDto.getId() == null) {
				Set<String> sets=destinationDto.getAliases().stream()
						 .map(alias -> alias.getName()).collect(Collectors.toSet());
						return aliasService.checkForDuplicateAlias(sets, DESTINATION,
								destinationDto.getClientId(), destinationDto.getCompanyId());
			} else {
				return aliasService.checkForDuplicateAlias(DESTINATION,destinationDto.getAliases(),destinationDto.getClientId(),
						destinationDto.getCompanyId());
			}
		}
		return null;
	}

	private ValidationError destinationCityValidations(DestinationDTO destinationDto) {
		if ((destinationDto.getType() != null && !destinationDto.getType().equals(DestinationType.CITY))
				&& hasNoValue(destinationDto.getCityId()).equals(Boolean.TRUE)) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(CITY_IS_MANDATORY_PLEASE_SELECT_CITY)
					.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError destinationIATAAirportCodeValidations(DestinationDTO destinationDto) {
		if ((destinationDto.getType() != null && destinationDto.getType().equals(DestinationType.AIRPORT))
				&& StringUtils.isBlank(destinationDto.getIataAirportCode())) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_DESTINATION_TYPE_IS_AIRPORT_THEN_IATA_AIRPORT_CODE_IS_MANDATORY)
					.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError destinationCountryValidations(DestinationDTO destinationDto) {
		if (Boolean.TRUE.equals(hasNoValue(destinationDto.getCountryId()))) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(COUNTRY_IS_MANDATORY_PLEASE_SELECT_COUNTRY)
					.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError destinationPortValidations(DestinationDTO destinationDto) {
		if ((destinationDto.getType() != null && destinationDto.getType().equals(DestinationType.TERMINAL))
				&& destinationDto.getPortId() == null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_DESTINATION_TYPE_IS_TERMINAL_THEN_PORT_IS_MANDATORY)
					.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError destinationStateValidations(DestinationDTO destinationDto) {
		if (destinationDto.getStateId() == null) {
			Optional<CountryDTO> countryDto=countryService.findOne(destinationDto.getCountryId());
			if(countryDto.isPresent()) {
				Boolean isStateMandatory = countryDto.get().getIsStateMandatory();
				if(isStateMandatory.equals(Boolean.TRUE)) {
					return ObjectValidationError.builder()
							.type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(FOR_A_GIVEN_COUNTY_STATE_IS_MANDATORY_PLEASE_ENTER_STATE)
							.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
							.build();
				}
			}
		}
		return null;
	}

	private Boolean hasNoValue(Long data) {
		return (data != null) ? Boolean.FALSE : Boolean.TRUE;
	}
	
	private ValidationError destinationCompanyValidations(DestinationDTO destinationDto) {
		if(destinationDto.getCompanyId()==0 || ObjectUtils.isEmpty(destinationDto.getCompanyId())) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(COMPANY_IS_REQUIRED)
					.referenceId(destinationDto.getId()==null?"":String.valueOf(destinationDto.getId()))
					.build();
		}
		return null;
	}


}
