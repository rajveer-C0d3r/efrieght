package com.grtship.client.validator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.client.adaptor.MasterModuleAdapter;
import com.grtship.client.adaptor.OAuthModuleAdapter;
import com.grtship.client.domain.Company;
import com.grtship.client.feignclient.OAuthModule;
import com.grtship.client.repository.CompanyRepository;
import com.grtship.client.util.SecurityUtils;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.AddressDTO;
import com.grtship.core.dto.CompanyDTO;
import com.grtship.core.dto.CsaDetailsDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.GstVatType;
import com.grtship.core.enumeration.UserType;
import com.grtship.core.enumeration.ValidationErrorType;

@Component
public class CompanyValidator implements Validator<Object> {

	private static final String ADD_AT_LEAST_ONE_CSA_USER = "Add at least one CSA user.";

	private static final String FINANCIAL_YEAR_ENDS_ON_IS_MANDATORY = "Financial year ends on is mandatory.";

	private static final String FINANCIAL_YEAR_STARTS_ON_IS_MANDATORY = "Financial year starts on is mandatory.";

	private static final String HEADQUARTER_CORPORATE_OFFICE_ADDRESS_IS_MANDATORY = "Headquarter / Corporate Office Address is mandatory.";

	private static final String EMAIL_ID_IS_MANDATORY = "Email id is mandatory.";

	private static final String COUNTRY_IS_MANDATORY = "Country is mandatory.";

	private static final String STATE_IS_MANDATORY = "State is mandatory.";

	private static final String CITY_IS_MANDATORY = "City is mandatory.";

	private static final String PINCODE_IS_MANDATORY = "Pincode is mandatory.";

	private static final String PAN_NO_INCOME_TAX_ID_IS_MANDATORY = "Pan No. / Income Tax ID is mandatory.";

	private static final String GST_VAT_TYPE_IS_MANDATORY = "GST/VAT Type is mandatory.";

	private static final String GST_NO_VAT_ID_SALES_TAX_ID_IS_MANDATORY = "GST No./VAT ID/Sales Tax ID is mandatory.";

	private static final String DATE_OF_INCORPORATION_IS_MANDATORY = "Date of incorporation is mandatory.";

	private static final String FINANCIAL_YEAR_OF_THE_COMPANY_IS_ALWAYS_FIXED_IS_MANDATORY = "Financial Year of the company is always fixed is mandatory.";

	private static final String CLIENT_NAME_IS_MANDATORY = "Client Name is mandatory.";

	private static final String CLIENT_CODE_IS_MANDATORY = "Client Code is mandatory.";

	private static final String COMPANY_PAN_NO_INCOME_TAX_ID_CAN_NOT_BE_DUPLICATE = "Company Pan No. / Income Tax ID can not be duplicate.";

	private static final String COMPANY_GST_NO_VAT_ID_SALES_TAX_ID_CAN_NOT_BE_DUPLICATE = "Company GST No./ VAT ID/Sales Tax ID can not be duplicate.";

	private static final String COMPANY_NAME_CAN_NOT_BE_DUPLICATE = "Company Name can not be duplicate.";

	private static final String COMPANY_CODE_CAN_NOT_BE_DUPLICATE = "Company Code can not be duplicate.";

	private static final String WITHHOLDING_TAX_ID_TAN_NO_TDS_ID_CAN_NOT_BE_DUPLICATE = "Withholding Tax ID / TAN No / TDS ID can not be duplicate.";

	private static final String WEF_DATE_CAN_T_BE_PAST_DATE = "WEF Date Can't Be Past Date.";
	
	private static final String REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING = "Request is already submitted for approval. You can.t update anything.";

	private static final String YOU_ENTERED_DUPLICATE_EMAIL_ID_EMAIL_MUST_BE_UNIQUE = "You Entered Duplicate Email Id for CSA Users, Email Id Must Be Unique.";

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private OAuthModuleAdapter oAuthModuleAdapter;

	@Autowired
	private MasterModuleAdapter masterModuleAdapter;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		List<ValidationError> errors = new LinkedList<>();
		if (action.equals("save")) {
			try {
				validateCompany((CompanyDTO) obj, errors);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (action.equals("update")) {
			try {
				validateUpdate((CompanyDTO) obj, errors);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (action.equals("deactivate")) {
			deactivateValidations((DeactivationDTO) obj, errors);
		}
		if (action.equals("reactivate")) {
			activateValidations((ReactivationDTO) obj, errors);
		}
		return errors;
	}
	
	@Autowired
	private OAuthModule authClient;

	public void validateCompany(CompanyDTO companyDto,List<ValidationError> errors) throws Exception {
		Optional<String> login = SecurityUtils.getCurrentUserLogin();
		Optional<UserType> userType = Optional.empty();
		if (login.isPresent())
			userType = oAuthModuleAdapter.getUserType(login.get());
		if (companyDto.getStatus() != null && !(companyDto.getStatus().equals(DomainStatus.DRAFT))) {
			if (userType.isPresent() && userType.get().equals(UserType.ADMIN)) {
				addNonEmpty(errors,validateClientCode(companyDto));
				addNonEmpty(errors, validateClientName(companyDto));
			} else {
				addNonEmpty(errors, validateEmailId(companyDto));
				validateAddress(companyDto,errors);
				addNonEmpty(errors, validatePanNo(companyDto));
				addNonEmpty(errors, validateGstVatType(companyDto));
				addNonEmpty(errors, validateGstNo(companyDto));
				addNonEmpty(errors, validateIncorporationDate(companyDto));
				addNonEmpty(errors, validateFixedFinancialYearFlag(companyDto));
				validateFinancialYearStartDateAndEndDate(companyDto,errors);
			}
			addNonEmpty(errors, validateCountry(companyDto));
			validateCsaDetails(companyDto,errors);
		} else if (companyDto.getStatus() != null && companyDto.getStatus().equals(DomainStatus.DRAFT)) {
			addNonEmpty(errors, validateEmailId(companyDto));
		}
		addNonEmpty(errors, validateWithHoldingTaxId(companyDto));
		addNonEmpty(errors, validateCompanyCode(companyDto));
		addNonEmpty(errors, validateCompanyName(companyDto));
		gstPanNoUniqueValidation(companyDto,errors);
	}

	private ValidationError validateWithHoldingTaxId(CompanyDTO companyDto) {
		if (companyDto.getWithholdingTaxId() != null) {
			List<Company> companies = companyRepository.findByWithholdingTaxId(companyDto.getWithholdingTaxId());
			Boolean companyPresent = companies.stream().anyMatch(company -> company.getId() != null
					&& companyDto.getId() != null && !(company.getId().equals(companyDto.getId())));
			if ((companyDto.getId() == null && !CollectionUtils.isEmpty(companies))
					|| companyPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(WITHHOLDING_TAX_ID_TAN_NO_TDS_ID_CAN_NOT_BE_DUPLICATE)
						.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
			}
		}
		return null;
	}

	private ValidationError validateCompanyCode(CompanyDTO companyDto) {
		List<Company> companies = companyRepository.findByCodeAndClientId(companyDto.getCode(),
				companyDto.getClientId());
		Boolean companyPresent = companies.stream().anyMatch(company -> company.getId() != null
				&& companyDto.getId() != null && !(company.getId().equals(companyDto.getId())));
		if ((companyDto.getId() == null && !CollectionUtils.isEmpty(companies))
				|| companyPresent.equals(Boolean.TRUE)) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COMPANY_CODE_CAN_NOT_BE_DUPLICATE)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		}
		return null;
	}

	private ValidationError validateCompanyName(CompanyDTO companyDto) {
		if (companyDto.getName() != null && companyDto.getClientId() != null && companyDto.getAddress() != null) {
			List<Company> companies = companyRepository.findByNameAndClientIdAndAddressCountryId(companyDto.getName(),
					companyDto.getClientId(), companyDto.getAddress().getCountryId());
			Boolean companyPresent = companies.stream().anyMatch(company -> company.getId() != null
					&& companyDto.getId() != null && !(company.getId().equals(companyDto.getId())));
			if ((companyDto.getId() == null && !CollectionUtils.isEmpty(companies))
					|| companyPresent.equals(Boolean.TRUE))
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COMPANY_NAME_CAN_NOT_BE_DUPLICATE)
						.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		}
		return null;
	}

	private void gstPanNoUniqueValidation(CompanyDTO companyDto,List<ValidationError> errors) {
		addNonEmpty(errors, gstNoUniqueValidation(companyDto));
		addNonEmpty(errors, panNoUniqueValidation(companyDto));
	}

	private ValidationError gstNoUniqueValidation(CompanyDTO companyDto) {
		if (companyDto.getGstNo() != null) {
			List<Company> companies = companyRepository.findByGstNo(companyDto.getGstNo());
			Boolean companyPresent = companies.stream().anyMatch(company -> company.getId() != null
					&& companyDto.getId() != null && !(company.getId().equals(companyDto.getId())));
			if ((companyDto.getId() == null && !CollectionUtils.isEmpty(companies))
					|| companyPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(COMPANY_GST_NO_VAT_ID_SALES_TAX_ID_CAN_NOT_BE_DUPLICATE)
						.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
			}
		}
		return null;
	}

	private ValidationError panNoUniqueValidation(CompanyDTO companyDto) {
		if (companyDto.getPanNo() != null) {
			List<Company> companies = companyRepository.findByPanNo(companyDto.getPanNo());
			Boolean companyPresent = companies.stream().anyMatch(company -> company.getId() != null
					&& companyDto.getId() != null && !(company.getId().equals(companyDto.getId())));
			if ((companyDto.getId() == null && !CollectionUtils.isEmpty(companies))
					|| companyPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(COMPANY_PAN_NO_INCOME_TAX_ID_CAN_NOT_BE_DUPLICATE)
						.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
			}
		}
		return null;
	}

	private ValidationError validateClientCode(CompanyDTO companyDto) {
		if (StringUtils.isBlank(companyDto.getClientCode()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(CLIENT_CODE_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateClientName(CompanyDTO companyDto) {
		if (StringUtils.isBlank(companyDto.getClientName()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(CLIENT_NAME_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateEmailId(CompanyDTO companyDto) {
		if (StringUtils.isBlank(companyDto.getEmailId()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(EMAIL_ID_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private void validateAddress(CompanyDTO companyDto, List<ValidationError> errors) {
		if (companyDto.getAddress() != null) {
			addNonEmpty(errors, validateHeadquarterAddress(companyDto));
			addNonEmpty(errors, validateState(companyDto.getAddress()));
			addNonEmpty(errors, validateCity(companyDto.getAddress()));
			addNonEmpty(errors, validatePincode(companyDto.getAddress()));
		}
	}

	private ValidationError validateHeadquarterAddress(CompanyDTO companyDto) {
		if (StringUtils.isBlank(companyDto.getAddress().getAddress()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(HEADQUARTER_CORPORATE_OFFICE_ADDRESS_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateCountry(CompanyDTO companyDto) {
		if (companyDto.getAddress() != null && companyDto.getAddress().getCountryId() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COUNTRY_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateState(AddressDTO address) {
		Boolean isStateMandatory = false;
		if (address.getCountryId() != null) {
			isStateMandatory = masterModuleAdapter.isStateMandatoryForGivenCountry(address.getCountryId());
			if (address.getStateId() == null && isStateMandatory) {
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(STATE_IS_MANDATORY).build();
			}
		}
		return null;
	}

	private ValidationError validateCity(AddressDTO address) {
		if (address.getCityId() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(CITY_IS_MANDATORY).build();
		return null;
	}

	private ValidationError validatePincode(AddressDTO address) {
		if (StringUtils.isBlank(address.getPincode()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(PINCODE_IS_MANDATORY).build();
		return null;
	}

	private ValidationError validatePanNo(CompanyDTO companyDto) {
		if (StringUtils.isBlank(companyDto.getPanNo()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(PAN_NO_INCOME_TAX_ID_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateGstVatType(CompanyDTO companyDto) {
		if (companyDto.getGstVatType() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(GST_VAT_TYPE_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateGstNo(CompanyDTO companyDto) {
		if (companyDto.getGstVatType() != null && !(companyDto.getGstVatType().equals(GstVatType.NONE))
				&& StringUtils.isBlank(companyDto.getGstNo()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(GST_NO_VAT_ID_SALES_TAX_ID_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateIncorporationDate(CompanyDTO companyDto) {
		if (companyDto.getIncorporationDate() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DATE_OF_INCORPORATION_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateFixedFinancialYearFlag(CompanyDTO companyDto) {
		if (companyDto.getFixedFinancialYearFlag() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(FINANCIAL_YEAR_OF_THE_COMPANY_IS_ALWAYS_FIXED_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private void validateFinancialYearStartDateAndEndDate(CompanyDTO companyDto, List<ValidationError> errors) {
		if (companyDto.getFixedFinancialYearFlag() != null
				&& companyDto.getFixedFinancialYearFlag().equals(Boolean.TRUE)) {
			addNonEmpty(errors, validateFinancialYearStartDate(companyDto));
			addNonEmpty(errors, validateFinancialYearEndDate(companyDto));
		}
	}

	private ValidationError validateFinancialYearStartDate(CompanyDTO companyDto) {
		if (companyDto.getFinancialYearStartMonth() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(FINANCIAL_YEAR_STARTS_ON_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

	private ValidationError validateFinancialYearEndDate(CompanyDTO companyDto) {
		if (companyDto.getFinancialYearEndMonth() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(FINANCIAL_YEAR_ENDS_ON_IS_MANDATORY)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build();
		return null;
	}

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

	public void activateValidations(ReactivationDTO activateDto, List<ValidationError> errors) {
		if (activateDto.getReactivationWefDate() != null
				&& activateDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors, ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(WEF_DATE_CAN_T_BE_PAST_DATE)
					.referenceId(
							activateDto.getReferenceId() == null ? "" : String.valueOf(activateDto.getReferenceId()))
					.build());
	}

	private void validateCsaDetails(CompanyDTO companyDto,List<ValidationError> errors) {
		if (CollectionUtils.isEmpty(companyDto.getCsaDetails()))
			addNonEmpty(errors,ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ADD_AT_LEAST_ONE_CSA_USER)
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build());
		
		if (!CollectionUtils.isEmpty(companyDto.getCsaDetails())) {
			List<String> csaDetailsToSave = companyDto.getCsaDetails().stream()
					.filter(csaDetailDto -> csaDetailDto.getEmail() != null).map(CsaDetailsDTO::getEmail)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(csaDetailsToSave)) {
				List<String> duplicateCsaDetails = csaDetailsToSave.stream()
						.filter(email -> Collections.frequency(csaDetailsToSave, email) > 1)
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateCsaDetails)) {
					addNonEmpty(errors,ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(YOU_ENTERED_DUPLICATE_EMAIL_ID_EMAIL_MUST_BE_UNIQUE)
							.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build());
				}
			}
		}
		String alreadyPresentEmails=authClient.validateCsaUsers(companyDto);
		if(ObjectUtils.isNotEmpty(alreadyPresentEmails)) {
			addNonEmpty(errors,ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message("Email Ids "+alreadyPresentEmails+" in CSA Details is already in use")
					.referenceId(companyDto.getId() == null ? "" : String.valueOf(companyDto.getId())).build());
		}
	}

	public void validateUpdate(CompanyDTO companyDto, List<ValidationError> errors) throws Exception {
		if (companyDto.getSubmittedForApproval() != null && companyDto.getSubmittedForApproval().equals(Boolean.TRUE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING);

		validateCompany(companyDto, errors);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addNonEmpty(List list, Object o) {
		if (null != o) {
			list.add(o);
		}
	}
}
