package com.grtship.client.validator;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.client.domain.CompanyBranch;
import com.grtship.client.mapper.CompanyBranchMapper;
import com.grtship.client.repository.CompanyBranchRepository;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.CompanyBranchCreationDTO;
import com.grtship.core.dto.CompanyBranchDTO;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.ValidationErrorType;

@Component
public class CompanyBranchValidator implements Validator<Object>{

	private static final String THIS_BRANCH_IS_ALREADY_DEACTIVATED = "This branch is already 'Deactivated'..!! ";
	private static final String THIS_BRANCH_IS_ALREADY_ACTIVE = "This Branch is already 'ACTIVE'..!! ";
	private static final String BRANCH_GST_NUMBER_IS_MANDATORY = "Branch GST Number Is Mandatory.";
	private static final String YOU_CAN_T_UPDATE_THIS_BRANCH_BECAUSE_PREVIOUS_APPROVAL_IS_PENDING_FOR_THIS_BRANCH = "You can't update this branch because previous Approval is pending for this Branch.";
	private static final String BRANCH_LOCATION_AND_CITY_MUST_NOT_BE_EMPTY = "Branch Location and City Must not be Empty..";
	private static final String REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivation WEF Date can't be past date.";
	private static final String DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivation WEF Date can't be past date.";
	private static final String BRANCH_NAME_CAN_T_BE_DUPLICATE = "Branch Name Can't be duplicate.";
	private static final String BRANCH_CODE_CAN_T_BE_DUPLICATE = "Branch Code Can't be duplicate.";

	@Autowired private CompanyBranchRepository branchRepository;
	
	@Autowired private CompanyBranchMapper companyBranchMapper;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		 List<ValidationError> errors=new LinkedList<>();
			if (action.equals("save")) {
				validateSave(companyBranchMapper.toEntity((CompanyBranchCreationDTO) obj), errors);
			}
			if (action.equals("update")) {
				validateUpdate(companyBranchMapper.toEntity((CompanyBranchDTO) obj), errors);
			}
			if (action.equals("deactivate")) {
				deactivateValidation((DeactivationDTO) obj, errors);
			}
			if (action.equals("reactivate")) {
				reactivateValidations((ReactivationDTO) obj, errors);
			}
			return errors;
	}

	public void validateSave(CompanyBranch companyBranch,List<ValidationError> errors) {
//		if(branchRepository.findByCodeAndClientId(companyBranch.getCode(),companyBranch.getClientId()).isPresent()) 
//			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, BRANCH_CODE_CAN_T_BE_DUPLICATE);
		addNonEmpty(errors,validateName(companyBranch));
		validateBranch(companyBranch,errors);
	}

	private ValidationError validateName(CompanyBranch companyBranch) {
		if (branchRepository.findByNameAndClientId(companyBranch.getName(), companyBranch.getClientId()).isPresent())
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_NAME_CAN_T_BE_DUPLICATE)
					.referenceId(companyBranch.getId() == null ? "" : String.valueOf(companyBranch.getId())).build();
		return null;
	}

	public void validateUpdate(CompanyBranch companyBranch,List<ValidationError> errors) {
		boolean isBranchPresent = branchRepository.findByIdAndStatus(companyBranch.getId(),DomainStatus.PENDING).isPresent();
		if(isBranchPresent)
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, YOU_CAN_T_UPDATE_THIS_BRANCH_BECAUSE_PREVIOUS_APPROVAL_IS_PENDING_FOR_THIS_BRANCH);
//		if(branchRepository.findByCodeAndClientIdAndIdNot(companyBranch.getCode(),companyBranch.getClientId(),companyBranch.getId()).isPresent()) 
//			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, BRANCH_CODE_CAN_T_BE_DUPLICATE);
		addNonEmpty(errors,validateNameOnUpdate(companyBranch));
		validateBranch(companyBranch,errors);
	}

	private ValidationError validateNameOnUpdate(CompanyBranch companyBranch) {
		if (branchRepository.findByNameAndClientIdAndIdNot(companyBranch.getName(), companyBranch.getClientId(),
				companyBranch.getId()).isPresent())
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_NAME_CAN_T_BE_DUPLICATE)
					.referenceId(companyBranch.getId() == null ? "" : String.valueOf(companyBranch.getId())).build();
		return null;
	}

	/** branch validation on non-DRAFT status.. */
	private void validateBranch(CompanyBranch companyBranch, List<ValidationError> errors) {
		if (companyBranch.getStatus() != null && !companyBranch.getStatus().equals(DomainStatus.DRAFT)) {
			addNonEmpty(errors, validateLocationAndCity(companyBranch));
			addNonEmpty(errors, validateGstNo(companyBranch));
		}
	}

	private ValidationError validateGstNo(CompanyBranch companyBranch) {
		if (CollectionUtils.isEmpty(companyBranch.getBranchGstDetails()))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_GST_NUMBER_IS_MANDATORY)
					.referenceId(companyBranch.getId() == null ? "" : String.valueOf(companyBranch.getId())).build();
		return null;
	}

	private ValidationError validateLocationAndCity(CompanyBranch companyBranch) {
		if (companyBranch.getStatus() != null && !companyBranch.getStatus().equals(DomainStatus.DRAFT)
				&& (companyBranch.getAddress() == null || (companyBranch.getAddress() != null
						&& (StringUtils.isEmpty(companyBranch.getAddress().getLocation()))
						|| companyBranch.getAddress().getCity() == null)))
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(BRANCH_LOCATION_AND_CITY_MUST_NOT_BE_EMPTY)
					.referenceId(companyBranch.getId() == null ? "" : String.valueOf(companyBranch.getId())).build();
		return null;
	}

	/**
	 * branch deactivate validation.....
	 */	
	public void deactivateValidation(@Valid DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());

		branchRepository.findById(deactivateDto.getReferenceId()).ifPresent(branch -> {
			if (branch.getDeactivateDtls() != null && branch.getDeactivateDtls().getDeactivatedDate() != null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(THIS_BRANCH_IS_ALREADY_DEACTIVATED)
								.referenceId(deactivateDto.getReferenceId() == null ? ""
										: String.valueOf(deactivateDto.getReferenceId()))
								.build());
		});
	}

	/**
	 * branch reactivate validation.....
	 */	
	public void reactivateValidations(ReactivationDTO reactivationDto, List<ValidationError> errors) {
		if (reactivationDto.getReactivationWefDate() != null
				&& reactivationDto.getReactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(reactivationDto.getReferenceId() == null ? ""
									: String.valueOf(reactivationDto.getReferenceId()))
							.build());

		branchRepository.findById(reactivationDto.getReferenceId()).ifPresent(branch -> {
			if (branch.getDeactivateDtls() != null && branch.getDeactivateDtls().getDeactivatedDate() == null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(THIS_BRANCH_IS_ALREADY_ACTIVE)
								.referenceId(reactivationDto.getReferenceId() == null ? ""
										: String.valueOf(reactivationDto.getReferenceId()))
								.build());
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addNonEmpty(List list, Object o) {
		if (null != o) {
			list.add(o);
		}
	}
}
