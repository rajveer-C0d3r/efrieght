package com.grtship.client.serviceimpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.client.adaptor.UserModuleAdaptor;
import com.grtship.client.feignclient.MasterModule;
import com.grtship.client.repository.ClientRepository;
import com.grtship.client.service.ClientValidatorService;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.BaseDTO;
import com.grtship.core.dto.ClientDTO;
import com.grtship.core.dto.CountryDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.DestinationDTO;
import com.grtship.core.dto.EmailPresentDto;
import com.grtship.core.dto.GsaDetailsDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.ValidationErrorType;

@Service
@Transactional(readOnly = true)
public class ClientValidatorServiceImpl implements ClientValidatorService,Validator<Object> {

	private static final String INVALID_CITY = "Invalid City.";

	private static final String CITY_IS_MANDATORY_PLEASE_SELECT_CITY = "City Is Mandatory, Please Select City.";

	private static final String ZIP_CODE_IS_MANDATORY_PLEASE_ENTER_ZIP_CODE = "Zip Code Is Mandatory, Please Enter Zip Code.";

	private static final String PAN_NO_INCOME_TAX_ID_ALREADY_EXISTS_ENTER_ANOTHER_PAN_NO_INCOME_TAX_ID = "PanNo IncomeTaxId Already Exists, Enter Another PanNo IncomeTaxId.";

	private static final String GST_VAT_SALES_TAX_ID_ALREADY_EXISTS_ENTER_ANOTHER_GST_VAT_SALES_TAX_ID = "Gst Vat SalesTax Id Already Exists, Enter Another Gst Vat SalesTax Id.";

	private static final String GSA_USER_DETAILS_ARE_MANDATORY_PLEASE_ENTER_GSA_USER_DETAILS = "Gsa User Details are Mandatory, Please Enter Gsa User Details.";

	private static final String INVALID_STATE = "Invalid State.";

	private static final String STATE_IS_MANDATORY_PLEASE_SELECT_STATE = "State Is Mandatory, Please Select State.";

	private static final String INVALID_COUNTRY = "Invalid Country.";
	
	private static final String REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING = "Request is already submitted for approval. You can.t update anything.";

	private static final String COUNTRY_IS_MANDATORY_PLEASE_SELECT_COUNTRY = "Country Is Mandatory, Please Select Country.";

	private static final String HEADQUARTER_CORPORATE_ADDRESS_IS_MANDATORY_PLEASE_ENTER_HEADQUARTER_CORPORATE_ADDRESS = "Headquarter Corporate Address Is Mandatory, Please Enter Headquarter Corporate Address";

	private static final String CLIENT_NAME_ALREADY_EXISTS_ENTER_ANOTHER_CLIENT_NAME = "Client Name Already Exists, Enter Another Client Name.";

	private static final String CLIENT_CODE_ALREADY_EXISTS_ENTER_ANOTHER_CLIENT_CODE = "Client Code Already Exists, Enter Another Client Code";

	private static final String WEF_DATE_CAN_T_BE_PAST_DATE = "WEF Date Can't Be Past Date.";
	
	private static final String YOU_ENTERED_DUPLICATE_EMAIL_ID_EMAIL_MUST_BE_UNIQUE = "You Entered Duplicate Email Id for GSA Users, Email Id Must Be Unique.";

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MasterModule masterModule;
	
	@Autowired
	private UserModuleAdaptor userModule;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
        List<ValidationError> errors=new LinkedList<>();
		if (action.equals("save")) {
			saveValidations((ClientDTO) obj, errors);
		}
		if (action.equals("update")) {
			updateValidations((ClientDTO) obj, errors);
		}
		if (action.equals("deactivate")) {
			deactivateValidations((DeactivationDTO) obj, errors);
		}
		if (action.equals("reactivate")) {
			activateValidations((ReactivationDTO) obj, errors);
		}
		return errors;
	}

	@Override
	public void saveValidations(ClientDTO clientDto,List<ValidationError> errors) {
		addNonEmpty(errors,validateCode(clientDto));
		addNonEmpty(errors,validateName(clientDto));
		validateAddress(clientDto,errors);
		addNonEmpty(errors,validatePanNoIncomeTaxId(clientDto));
		addNonEmpty(errors,validateGstVatSalesTaxId(clientDto));
		addNonEmpty(errors,validateGsaUserDetails(clientDto));
		validateGsaUserEmailIds(clientDto,errors);
	}

	private ValidationError validateCode(ClientDTO clientDto) {
		if (clientDto.getId() == null) {
			if (!CollectionUtils.isEmpty(clientRepository.findByCode(clientDto.getCode()))) {
				return returnCodeValidationError(clientDto);
			}
		} else {
			if (clientRepository.findByCodeAndIdNot(clientDto.getCode(), clientDto.getId()) != null) {
				return returnCodeValidationError(clientDto);
			}
		}
		return null;

	}

	private ValidationError returnCodeValidationError(ClientDTO clientDto) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(CLIENT_CODE_ALREADY_EXISTS_ENTER_ANOTHER_CLIENT_CODE)
				.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
	}

	private ValidationError validateName(ClientDTO clientDto) {
		if (clientDto.getId() == null) {
			if (!CollectionUtils.isEmpty(clientRepository.findByName(clientDto.getName()))) {
				return returnNameValidationError(clientDto);
			}
		} else {
			if (clientRepository.findByNameAndIdNot(clientDto.getName(), clientDto.getId()) != null) {
				return returnNameValidationError(clientDto);
			}
		}
		return null;

	}

	private ValidationError returnNameValidationError(ClientDTO clientDto) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(CLIENT_NAME_ALREADY_EXISTS_ENTER_ANOTHER_CLIENT_NAME)
				.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
	}

	private void validateAddress(ClientDTO clientDto,List<ValidationError> errors) {
		AddressDTO addressDto = clientDto.getAddress();
		if (addressDto != null) {
			addNonEmpty(errors,validateCountry(addressDto));
			addNonEmpty(errors,validateCity(addressDto));
			addNonEmpty(errors,validateCorporateAddress(addressDto));
		}
	}

	private ValidationError validateCorporateAddress(AddressDTO addressDto) {
		if (StringUtils.isBlank(addressDto.getAddress())) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(HEADQUARTER_CORPORATE_ADDRESS_IS_MANDATORY_PLEASE_ENTER_HEADQUARTER_CORPORATE_ADDRESS)
					.build();
		}
		return null;
	}

	private ValidationError validateCountry(AddressDTO addressDto) {
		if (Boolean.TRUE.equals(hasNoValue(addressDto.getCountryId()))) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COUNTRY_IS_MANDATORY_PLEASE_SELECT_COUNTRY)
					.build();
		} else {
			List<CountryDTO> countriesByIds = masterModule
					.getCountriesByIds(new HashSet<>(Arrays.asList(addressDto.getCountryId())));
			if (CollectionUtils.isEmpty(countriesByIds)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(INVALID_COUNTRY).build();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void validateState(AddressDTO addressDto) {
		Boolean isStateMandatory = false;
		if (addressDto.getCountryId() != null) {
			isStateMandatory = masterModule.isStateMandatoryForGivenCountry(addressDto.getCountryId());
			if (hasNoValue(addressDto.getStateId()) && isStateMandatory) {
				throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, STATE_IS_MANDATORY_PLEASE_SELECT_STATE);
			}
			if (Boolean.FALSE.equals(hasNoValue(addressDto.getStateId()))) {
				List<BaseDTO> states = masterModule.getStatesByIds(Arrays.asList(addressDto.getStateId()));
				if (CollectionUtils.isEmpty(states)) {
					throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, INVALID_STATE);
				}
			}
		}
	}

	private ValidationError validateCity(AddressDTO addressDto) {
		if (Boolean.TRUE.equals(hasNoValue(addressDto.getCityId()))) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(CITY_IS_MANDATORY_PLEASE_SELECT_CITY).build();
		} else {
			List<DestinationDTO> cities = masterModule.getDestinationsByIds(Arrays.asList(addressDto.getCityId()));
			if (CollectionUtils.isEmpty(cities)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(INVALID_CITY).build();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void validateZipcode(AddressDTO addressDto) {
		if (StringUtils.isBlank(addressDto.getPincode())) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, ZIP_CODE_IS_MANDATORY_PLEASE_ENTER_ZIP_CODE);
		}
	}

	private ValidationError validatePanNoIncomeTaxId(ClientDTO clientDto) {
		if (clientDto.getId() == null) {
			if (clientRepository.findByPanNo(clientDto.getPanNo()) != null) {
				return returnPanNoValidationError(clientDto);
			}
		} else {
			if (clientRepository.findByPanNoAndIdNot(clientDto.getPanNo(), clientDto.getId()) != null) {
				return returnPanNoValidationError(clientDto);
			}
		}
		return null;
	}

	private ValidationError returnPanNoValidationError(ClientDTO clientDto) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(PAN_NO_INCOME_TAX_ID_ALREADY_EXISTS_ENTER_ANOTHER_PAN_NO_INCOME_TAX_ID)
				.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
	}

	private ValidationError validateGstVatSalesTaxId(ClientDTO clientDto) {
		if (clientDto.getId() == null) {
			if (clientDto.getSalesTaxId() != null && clientRepository.findBySalesTaxId(clientDto.getSalesTaxId()) != null) {
				return returnGstNoValidationError(clientDto);
			}
		} else {
			if (clientDto.getSalesTaxId() != null && clientRepository.findBySalesTaxIdAndIdNot(clientDto.getSalesTaxId(), clientDto.getId()) != null) {
				return returnGstNoValidationError(clientDto);
			}
		}
		return null;
	}

	private ValidationError returnGstNoValidationError(ClientDTO clientDto) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(GST_VAT_SALES_TAX_ID_ALREADY_EXISTS_ENTER_ANOTHER_GST_VAT_SALES_TAX_ID)
				.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
	}

	private ValidationError validateGsaUserDetails(ClientDTO clientDto) {
		if (CollectionUtils.isEmpty(clientDto.getGsaDetails())) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(GSA_USER_DETAILS_ARE_MANDATORY_PLEASE_ENTER_GSA_USER_DETAILS)
					.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
		}
		return null;
	}

	private Boolean hasNoValue(Long data) {
		return (data == null) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public void deactivateValidations(DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());
	}

	@Override
	public void activateValidations(ReactivationDTO activateDto, List<ValidationError> errors) {
		if (activateDto.getReactivationWefDate() != null
				&& activateDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors, ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(WEF_DATE_CAN_T_BE_PAST_DATE)
					.referenceId(
							activateDto.getReferenceId() == null ? "" : String.valueOf(activateDto.getReferenceId()))
					.build());
	}

	@Override
	public void updateValidations(ClientDTO clientDto, List<ValidationError> errors) {
		if (clientDto.getSubmittedForApproval() != null && clientDto.getSubmittedForApproval().equals(Boolean.TRUE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING);
		saveValidations(clientDto, errors);
	}

	private void validateGsaUserEmailIds(ClientDTO clientDto,List<ValidationError> errors) {
		addNonEmpty(errors,checkDuplicateEmail(clientDto));
		addNonEmpty(errors,checkAlreadyExistEmails(clientDto));
	}

	private ValidationError checkAlreadyExistEmails(ClientDTO clientDto) {
		if (!CollectionUtils.isEmpty(clientDto.getGsaDetails())) {
			List<String> emailIds = clientDto.getGsaDetails().stream()
					.filter(gsaDetail -> (StringUtils.isNotEmpty(gsaDetail.getEmail())
							&& ObjectUtils.isEmpty(gsaDetail.getId())))
					.map(GsaDetailsDTO::getEmail).collect(Collectors.toList());
			EmailPresentDto checkEmailPresent = userModule.checkEmailPresent(emailIds);
			if (ObjectUtils.isNotEmpty(checkEmailPresent)) {
				if (!CollectionUtils.isEmpty(checkEmailPresent.getEmailIds())) {
					String alreadyExistEmails = checkEmailPresent.getEmailIds().stream().map(Object::toString)
							.collect(Collectors.joining(",")) + " Already present";
					return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(alreadyExistEmails)
							.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addNonEmpty(List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}

	private ValidationError checkDuplicateEmail(ClientDTO clientDto) {
		if (!CollectionUtils.isEmpty(clientDto.getGsaDetails())) {
			List<String> gsaDetailsToSave = clientDto.getGsaDetails().stream()
					.filter(gsaDetailDto -> gsaDetailDto.getEmail() != null).map(GsaDetailsDTO::getEmail)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(gsaDetailsToSave)) {
				List<String> duplicateGsaDetails = gsaDetailsToSave.stream()
						.filter(email -> Collections.frequency(gsaDetailsToSave, email) > 1)
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateGsaDetails)) {
					return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_EMAIL_ID_EMAIL_MUST_BE_UNIQUE)
							.referenceId(clientDto.getId() == null ? "" : String.valueOf(clientDto.getId())).build();
				}
			}
		}
		return null;
	}

}
