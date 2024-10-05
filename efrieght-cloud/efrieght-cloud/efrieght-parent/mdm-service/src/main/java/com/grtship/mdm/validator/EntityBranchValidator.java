package com.grtship.mdm.validator;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.common.exception.BadRequestAlertException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.EntityBranchDTO;
import com.grtship.core.dto.EntityBranchRequestDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.EntityBranch;
import com.grtship.mdm.mapper.EntityBranchMapper;
import com.grtship.mdm.repository.EntityBranchRepository;
import com.grtship.mdm.service.CountryService;
import com.grtship.mdm.service.ExternalEntityQueryService;

@Component
public class EntityBranchValidator implements Validator<Object>{

	private static final String BRANCH_CODE_CAN_T_BE_DUPLICATE = "Branch Code can't be duplicate.";

	private static final String YOU_CAN_T_UPDATE_THIS_BRANCH_BECAUSE_APPROVAL_FOR_THIS_BRANCH_IS_PENDING = "You can't Update this Branch because approval for this branch is pending.";

	private static final String IF_SEZ_IS_TRUE_THEN_SEZ_WEF_DATE_AND_SEZ_VALID_UPTO_DATE_CAN_T_BE_NULL = "If SEZ is true then 'SEZ WEF Date' and 'SEZ Valid Upto Date' Can't Be Null.";

	private static final String BRANCH_CITY_IS_MANDATORY = "Branch City Is Mandatory.";

	private static final String IF_STATE_IS_MANDATORY_FOR_ENTERED_COUNTRY_THEN_PLEASE_ENTERED_STATE_AS_STATE_CAN_T_BE_NULL = "If State is mandatory for entered country then please entered state as state can't be null";

	private static final String LOCATION_CAN_T_BE_NULL = "Location Can't Be Null.";

	private static final String BRANCH_LOCATION_AND_BRANCH_CITY_IS_MANDATORY = "Branch Location and Branch City is Mandatory.";

	private static final String YOU_HAVE_SELECTED_BRANCH_AS_A_VENDOR_THEN_VENDOR_CREDIT_TERMS_ARE_MANDATORY = "You have selected branch as a vendor then vendor credit terms are mandatory.";

	private static final String YOU_HAVE_SELECTED_BRANCH_AS_A_CUSTOMER_THEN_CUSTOMER_CREDIT_TERMS_ARE_MANDATORY = "You have selected branch as a customer then customer credit terms are mandatory.";

	private static final String FOR_BRANCH_CUSTOMER_OR_VENDOR_IS_MANDATORY = "For Branch Customer Or Vendor is mandatory.";

	private static final String YOUR_ENTERED_ENTITY_NAME_IS_ALREADY_TAKEN_PLEASE_ENTER_ANOTHER_NAME_AND_PROCEED = "Your Entered Branc Name is already taken. Please Enter another Name and proceed..!";

	private static final String FOR_ENTITY_YOU_ARE_TRYING_TO_CREATE_BRANCH_IS_NOT_EXIST = "For Entity you are trying to create branch is not exist..!";

	private static final String ENTITY_ID_CAN_T_BE_NULL = "Entity Id can't be null";

	private static final String DEACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivate WEF Date can't be past Date.";
	
	private static final String REACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivate WEF Date can't be past Date.";

	private static final String EMPTY_STRING = "";

	private static final String ENTITY_BRANCH = "Entity Branch";
	
	private static final String COMPANY_IS_REQUIRED="Company is required";

	@Autowired
	private CountryService countryservice;

	@Autowired
	private ExternalEntityQueryService entityQueryService;

	@Autowired
	private EntityBranchRepository branchDetailsRepository;
	
	@Autowired
	private EntityBranchMapper branchDetailsMapper;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if (action.equals("save")) {
			addNonEmpty(errors,creditTermsValidation((EntityBranchRequestDTO) obj));
			saveValidate(branchDetailsMapper.toEntity((EntityBranchRequestDTO) obj), errors);
		}
		if (action.equals("update")) {
			addNonEmpty(errors,creditTermsValidation((EntityBranchDTO) obj));
			updateValidate(branchDetailsMapper.toEntity((EntityBranchDTO) obj),errors);
		}
		if (action.equals("deactivate")) {
			branchDeactivateValidation((DeactivationDTO) obj,errors);
		}
		if (action.equals("reactivate")) {
			branchReactivateValidation((ReactivationDTO) obj, errors);
		}
		return errors;
	}

	public void saveValidate(EntityBranch branchDetails, List<ValidationError> errors) {
		addNonEmpty(errors, validateCompanyId(branchDetails));
		addNonEmpty(errors, validateAddress(branchDetails));
		addNonEmpty(errors, validateCityAndLocation(branchDetails));
		addNonEmpty(errors, validateExternalEntity(branchDetails));
		addNonEmpty(errors, validateExternalEntityId(branchDetails));
		addNonEmpty(errors, validateEntityName(branchDetails));
		addNonEmpty(errors, validateEntityCode(branchDetails));

		if (branchDetails.getStatus() != null && !branchDetails.getStatus().equals(DomainStatus.DRAFT)) {
			validateEntityBranch(branchDetails,errors);
		}
	}

	private ValidationError validateExternalEntityId(EntityBranch branchDetails) {
		if(branchDetails.getExternalEntity() != null) {
			if (!entityQueryService.existById(branchDetails.getExternalEntity().getId()))
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(FOR_ENTITY_YOU_ARE_TRYING_TO_CREATE_BRANCH_IS_NOT_EXIST)
						.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();			
		}
		return null;
	}

	private ValidationError validateExternalEntity(EntityBranch branchDetails) {
		if (branchDetails.getExternalEntity() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ENTITY_ID_CAN_T_BE_NULL)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateCityAndLocation(EntityBranch branchDetails) {
		if (branchDetails.getAddress() != null && (branchDetails.getAddress().getCity() == null
				|| branchDetails.getAddress().getLocation() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_LOCATION_AND_BRANCH_CITY_IS_MANDATORY)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateAddress(EntityBranch branchDetails) {
		if (branchDetails.getAddress() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_LOCATION_AND_BRANCH_CITY_IS_MANDATORY)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateCompanyId(EntityBranch branchDetails) {
		if (branchDetails.getCompanyId() == null || branchDetails.getCompanyId() == 0) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(COMPANY_IS_REQUIRED)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		}
		return null;
	}
	
	/**
	 * code duplicate validation
	 */
	private ValidationError validateEntityCode(EntityBranch branchDetails) {
		List<Long> banchIds = branchDetailsRepository.getIdByCodeAndClientId(branchDetails.getCode(),
				branchDetails.getClientId());
		Boolean entityByCodeFlag = banchIds.stream()
				.filter(obj -> obj != null && branchDetails.getId() != null && !(obj.equals(branchDetails.getId())))
				.map(obj -> obj).findAny().isPresent();
		if ((branchDetails.getId() == null && !CollectionUtils.isEmpty(banchIds))
				|| (branchDetails.getId() != null && entityByCodeFlag.equals(Boolean.TRUE)))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_CODE_CAN_T_BE_DUPLICATE)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	/**
	 * name duplicate validation
	 */
	private ValidationError validateEntityName(EntityBranch branchDetails) {
		List<Long> banchIds = branchDetailsRepository.getIdByNameAndClientId(branchDetails.getName(),
				branchDetails.getClientId());
		Boolean entityByNameFlag = banchIds.stream()
				.filter(id -> id != null && branchDetails.getId() != null && !(id.equals(branchDetails.getId())))
				.map(obj -> obj).findAny().isPresent();
		if ((branchDetails.getId() == null && !CollectionUtils.isEmpty(banchIds))
				|| (branchDetails.getId() != null && entityByNameFlag.equals(Boolean.TRUE)))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(YOUR_ENTERED_ENTITY_NAME_IS_ALREADY_TAKEN_PLEASE_ENTER_ANOTHER_NAME_AND_PROCEED)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	/**
	 * Entity Branch Validations.
	 **/
	private void validateEntityBranch(EntityBranch branchDetails, List<ValidationError> errors) {
		addNonEmpty(errors, validateSEZ(branchDetails));
		addNonEmpty(errors, validateLocation(branchDetails));
		addNonEmpty(errors, validateState(branchDetails));
		addNonEmpty(errors, validateCity(branchDetails));

	}

	private ValidationError validateCity(EntityBranch branchDetails) {
		if (branchDetails.getAddress() != null && branchDetails.getAddress().getCity()==null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_CITY_IS_MANDATORY)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateState(EntityBranch branchDetails) {
		if (branchDetails.getAddress() != null
				&& ((countryservice.isStateMandatoryForGivenCountry(branchDetails.getAddress().getCountry().getId()))
						.equals(Boolean.TRUE))
				&& branchDetails.getAddress().getState().getId() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_STATE_IS_MANDATORY_FOR_ENTERED_COUNTRY_THEN_PLEASE_ENTERED_STATE_AS_STATE_CAN_T_BE_NULL)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateSEZ(EntityBranch branchDetails) {
		if (branchDetails.getSez().equals(Boolean.TRUE)
				&& (branchDetails.getSezWEFDate() == null || branchDetails.getSezValidUptoDate() == null))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_SEZ_IS_TRUE_THEN_SEZ_WEF_DATE_AND_SEZ_VALID_UPTO_DATE_CAN_T_BE_NULL)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	private ValidationError validateLocation(EntityBranch branchDetails) {
		if (branchDetails.getAddress() != null && branchDetails.getAddress().getLocation() == null)
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(LOCATION_CAN_T_BE_NULL)
					.referenceId(branchDetails.getId() == null ? "" : String.valueOf(branchDetails.getId())).build();
		return null;
	}

	/**
	 * validate credit terms..
	 **/
	public ValidationError creditTermsValidation(EntityBranchRequestDTO branchDetailsDto) {
		if (branchDetailsDto.getStatus() != null && !branchDetailsDto.getStatus().equals(DomainStatus.DRAFT)) {
			if (branchDetailsDto.getCustomerFlag().equals(Boolean.FALSE)
					&& branchDetailsDto.getVendorFlag().equals(Boolean.FALSE))
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR).message(FOR_BRANCH_CUSTOMER_OR_VENDOR_IS_MANDATORY)
						.referenceId(branchDetailsDto.getId() == null ? "" : String.valueOf(branchDetailsDto.getId()))
						.build();
			if (branchDetailsDto.getCustomerFlag().equals(Boolean.TRUE)
					&& (branchDetailsDto.getCustomerEntityLevel() == null
							|| branchDetailsDto.getCustomerCreditAmount() == null
							|| branchDetailsDto.getCustomerCreditDays() == null))
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(YOU_HAVE_SELECTED_BRANCH_AS_A_CUSTOMER_THEN_CUSTOMER_CREDIT_TERMS_ARE_MANDATORY)
						.referenceId(branchDetailsDto.getId() == null ? "" : String.valueOf(branchDetailsDto.getId()))
						.build();

			if (branchDetailsDto.getVendorFlag().equals(Boolean.TRUE)
					&& (branchDetailsDto.getVendorEntityLevel() == null
							|| branchDetailsDto.getVendorCreditAmount() == null
							|| branchDetailsDto.getVendorCreditDays() == null))
				return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(YOU_HAVE_SELECTED_BRANCH_AS_A_VENDOR_THEN_VENDOR_CREDIT_TERMS_ARE_MANDATORY)
						.referenceId(branchDetailsDto.getId() == null ? "" : String.valueOf(branchDetailsDto.getId()))
						.build();
		}
		return null;
	}

	/** branch deactivate validation.. */
	public void branchDeactivateValidation(@Valid DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());

	}
	
	/** branch reactivate validation.. */
	public void branchReactivateValidation(@Valid ReactivationDTO reactivateDto, List<ValidationError> errors) {
		if (reactivateDto.getReactivationWefDate() != null
				&& reactivateDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(REACTIVATE_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(reactivateDto.getReferenceId() == null ? ""
									: String.valueOf(reactivateDto.getReferenceId()))
							.build());
	}

	public void updateValidate(EntityBranch branchDetails,List<ValidationError> errors) {
		if (branchDetails.getDeactivateDtls() != null
				&& branchDetails.getDeactivateDtls().getDeactivationWefDate() != null)
			throw new BadRequestAlertException(YOU_CAN_T_UPDATE_THIS_BRANCH_BECAUSE_APPROVAL_FOR_THIS_BRANCH_IS_PENDING,
					ENTITY_BRANCH, EMPTY_STRING);

		saveValidate(branchDetails,errors);
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
