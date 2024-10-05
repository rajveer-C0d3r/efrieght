package com.grtship.account.validator.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.account.domain.Group;
import com.grtship.account.repository.GroupRepository;
import com.grtship.account.service.ObjectAliasService;
import com.grtship.account.validator.GroupValidator;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.GroupCreationDTO;
import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.enumeration.NatureOfGroup;
import com.grtship.core.enumeration.ValidationErrorType;

@Component
public class GroupValidatorImpl implements GroupValidator,Validator<Object> {

	private static final String IF_NATURE_OF_GROUP_IS_EXPENSE_THEN_EXPENSE_TYPE_IS_MANDATORY = "If Nature of group is Expense then Expense Type is Mandatory.";

	private static final String IF_NATURE_OF_GROUP_IS_INCOME_THEN_INCOME_TYPE_IS_MANDATORY = "If Nature of group is Income then Income Type is Mandatory.";

	private static final String GROUP_IS_ALREADY_DEACTIVATED = "Group is already Deactivated.";

	private static final String THIS_GROUP_IS_ALREADY_ACTIVE = "This group is already active.";

	private static final String REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING = "Request is already submitted for approval. You can.t update anything.";

	private static final String REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivation WEF Date Can't be past Date.";

	private static final String DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivation WEF Date can't be past Date.";

	private static final String UNABLE_TO_DELETE_THIS_GROUP_IS_ASSOCIATE_WITH_OTHER_GROUPS = "Unable to delete. This group is associate with other groups.";

	private static final String WHEN_UNDER_IS_PRIMARY_THEN_NATURE_OF_GROUP_IS_MANDATORY = "When under is primary, then nature of group is mandatory.";

	private static final String WHEN_NATURE_OF_GROUP_IS_INCOME_OR_EXPENSE_THEN_DOES_IT_AFFECTS_GROSS_PROFIT_IS_MANDATORY = "When nature of group is Income Or Expense then Does it affects gross profit is mandatory.";

	private static final String GROUP_BEHAVES_LIKE_A_SUB_GROUP_IS_MANDATORY = "Group behaves like a sub-group is mandatory.";

	private static final String GROUP_NAME_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_GROUP_NAME = "Group name already exists, please enter another Group name.";

	private static final String GROUP_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_GROUP_CODE = "Group code already exists, please enter another Group code.";

	private static final String GROUP = "Group";

	private static final String YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE = "You Entered Duplicate Aliases, Aliases Must Be Unique.";

	private static final String COMPANY_IS_REQUIRED="Company is required";
	
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ObjectAliasService aliasService;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		List<ValidationError> errors=new LinkedList<>();
	    if (action.equals("save") || action.equals("update")) {
		    validateGroup((GroupCreationDTO) obj,errors);
	    }
		if (action.equals("deactivate")) {
			deactivateValidations((DeactivationDTO) obj,errors);
		}
		if (action.equals("reactivate")) {
			reactivateValidations((ReactivationDTO) obj, errors);
		}
		return errors;
	}

	@Override
	public void validateGroup(GroupCreationDTO groupDto,List<ValidationError> errors) {
		addNonEmpty(errors,validateCompanyId(groupDto));
		addNonEmpty(errors,validateGroupCode(groupDto));
		addNonEmpty(errors,validateGroupName(groupDto));
		addNonEmpty(errors,validateGroupAlias(groupDto));
		addNonEmpty(errors,validateNatureOfGroup(groupDto));
		addNonEmpty(errors,validateAffectsGrossProfit(groupDto));
		addNonEmpty(errors,validateSubGroupFlag(groupDto));
		addNonEmpty(errors,validateDirectIncomeFlag(groupDto));
		addNonEmpty(errors,validateDirectExpenseFlag(groupDto));
	}

	private ValidationError validateCompanyId(GroupCreationDTO groupDto) {
		if(groupDto.getCompanyId()==0 || groupDto.getCompanyId()==null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(COMPANY_IS_REQUIRED)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		}
		return null;
	}
	
	private ValidationError validateGroupAlias(GroupCreationDTO groupDto) {
		if (!CollectionUtils.isEmpty(groupDto.getAliases())) {
			List<String> aliasesToSave = groupDto.getAliases().stream()
					.filter(objectAliasDTO -> objectAliasDTO.getName() != null).map(ObjectAliasDTO::getName)
					.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(aliasesToSave)) {
				List<String> duplicateAliases = aliasesToSave.stream()
						.filter(name -> Collections.frequency(aliasesToSave, name) > 1).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(duplicateAliases)
						|| !CollectionUtils.isEmpty(groupRepository.findByCodeInAndClientIdAndCompanyId(aliasesToSave,
								groupDto.getClientId(), groupDto.getCompanyId()))
						|| !CollectionUtils.isEmpty(groupRepository.findByNameInAndClientIdAndCompanyId(aliasesToSave,
								groupDto.getClientId(), groupDto.getCompanyId()))) {
					return ObjectValidationError.builder()
							.type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR)
							.message(YOU_ENTERED_DUPLICATE_ALIASES_ALIASES_MUST_BE_UNIQUE)
							.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
							.build();
				}
			}

			if (groupDto.getId() == null) {
				Set<String> sets=groupDto.getAliases().stream()
						 .map(alias -> alias.getName()).collect(Collectors.toSet());
					return aliasService.checkForDuplicateAlias(sets, GROUP,
								groupDto.getClientId(), groupDto.getCompanyId());
			} else {
				aliasService.checkForDuplicateEntityAlias(groupDto.getAliases(),GROUP,groupDto.getClientId(),
						groupDto.getCompanyId());
			}
		}
		return null;
	}

	private ValidationError validateGroupCode(GroupCreationDTO groupDto) {
		if (groupDto.getId() == null) {
			if (groupRepository.findByCodeAndClientIdAndCompanyId(groupDto.getCode(), groupDto.getClientId(),
					groupDto.getCompanyId()) != null
					|| groupRepository.findByNameAndClientIdAndCompanyId(groupDto.getCode(), groupDto.getClientId(),
							groupDto.getCompanyId()) != null
					|| checkForDuplication(groupDto.getCode(), groupDto.getClientId(), groupDto.getCompanyId())
							.equals(Boolean.TRUE))
				return returnGroupCodeValidationError(groupDto);
			    
		} else {
			if (groupRepository.findByIdNotAndCodeAndClientIdAndCompanyId(groupDto.getId(), groupDto.getCode(),
					groupDto.getClientId(), groupDto.getCompanyId()) != null
					|| groupRepository.findByIdNotAndNameAndClientIdAndCompanyId(groupDto.getId(), groupDto.getCode(),
							groupDto.getClientId(), groupDto.getCompanyId()) != null)
				return returnGroupCodeValidationError(groupDto);
		}
		return null;
	}

	private ValidationError returnGroupCodeValidationError(GroupCreationDTO groupDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(GROUP_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_GROUP_CODE)
				.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
				.build();
	}

	private ValidationError validateGroupName(GroupCreationDTO groupDto) {
		if (groupDto.getId() == null) {
			if (groupRepository.findByNameAndClientIdAndCompanyId(groupDto.getName(), groupDto.getClientId(),
					groupDto.getCompanyId()) != null
					|| groupRepository.findByCodeAndClientIdAndCompanyId(groupDto.getName(), groupDto.getClientId(),
							groupDto.getCompanyId()) != null
					|| checkForDuplication(groupDto.getName(), groupDto.getClientId(), groupDto.getCompanyId())
							.equals(Boolean.TRUE))
				return returnGroupNameValidationError(groupDto);
		} else {
			if (groupRepository.findByIdNotAndNameAndClientIdAndCompanyId(groupDto.getId(), groupDto.getName(),
					groupDto.getClientId(), groupDto.getCompanyId()) != null
					|| groupRepository.findByIdNotAndCodeAndClientIdAndCompanyId(groupDto.getId(), groupDto.getName(),
							groupDto.getClientId(), groupDto.getCompanyId()) != null)
				return returnGroupNameValidationError(groupDto);
		}
		return null;
	}

	private ValidationError returnGroupNameValidationError(GroupCreationDTO groupDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(GROUP_NAME_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_GROUP_NAME)
				.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
				.build();
	}

	private Boolean checkForDuplication(String alias, Long clientId, Long companyId) {
		try {
			aliasService.checkForDuplicateAlias(alias, GROUP, clientId, companyId);
		} catch (InvalidDataException invalidDataException) {
			return true;
		}
		return false;
	}

	private ValidationError validateSubGroupFlag(GroupCreationDTO groupDto) {
		if (groupDto.getParentGroupId() != null && groupDto.getSubGroupFlag() == null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(GROUP_BEHAVES_LIKE_A_SUB_GROUP_IS_MANDATORY)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError validateAffectsGrossProfit(GroupCreationDTO groupDto) {
		if (groupDto.getNatureOfGroup() != null
				&& (groupDto.getNatureOfGroup().equals(NatureOfGroup.INCOME)
						|| groupDto.getNatureOfGroup().equals(NatureOfGroup.EXPENSE))
				&& groupDto.getAffectsGrossProfitFlag() == null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(WHEN_NATURE_OF_GROUP_IS_INCOME_OR_EXPENSE_THEN_DOES_IT_AFFECTS_GROSS_PROFIT_IS_MANDATORY)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError validateNatureOfGroup(GroupCreationDTO groupDto) {
		if (groupDto.getParentGroupId() == null && groupDto.getNatureOfGroup() == null) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(WHEN_UNDER_IS_PRIMARY_THEN_NATURE_OF_GROUP_IS_MANDATORY)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		}
		return null;

	}

	private ValidationError validateDirectExpenseFlag(GroupCreationDTO groupDto) {
		if (groupDto.getParentGroupId() == null && groupDto.getNatureOfGroup() != null
				&& groupDto.getNatureOfGroup().equals(NatureOfGroup.INCOME) && groupDto.getDirectIncomeFlag() == null)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_NATURE_OF_GROUP_IS_INCOME_THEN_INCOME_TYPE_IS_MANDATORY)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		return null;
	}

	private ValidationError validateDirectIncomeFlag(GroupCreationDTO groupDto) {
		if (groupDto.getParentGroupId() == null && groupDto.getNatureOfGroup() != null
				&& groupDto.getNatureOfGroup().equals(NatureOfGroup.EXPENSE) && groupDto.getDirectExpenseFlag() == null)
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(IF_NATURE_OF_GROUP_IS_EXPENSE_THEN_EXPENSE_TYPE_IS_MANDATORY)
					.referenceId(groupDto.getId()==null?"":String.valueOf(groupDto.getId()))
					.build();
		return null;
	}

	@Override
	public void deleteValidations(Long id) {
		List<Group> childGroups = groupRepository.findByTreeIdContaining(id + ".");
		if (!CollectionUtils.isEmpty(childGroups)) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					UNABLE_TO_DELETE_THIS_GROUP_IS_ASSOCIATE_WITH_OTHER_GROUPS);
		}

	}

	/**
	 * group deactivate validation.....
	 */
	@Override
	public void deactivateValidations(DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());

		groupRepository.findById(deactivateDto.getReferenceId()).ifPresent(group -> {
			if (group.getDeactivate() != null && group.getDeactivate().getDeactivatedDate() != null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(GROUP_IS_ALREADY_DEACTIVATED)
								.referenceId(deactivateDto.getReferenceId() == null ? ""
										: String.valueOf(deactivateDto.getReferenceId()))
								.build());
		});
	}

	/**
	 * group reactivate validation.....
	 */
	@Override
	public void reactivateValidations(ReactivationDTO reactivationDto, List<ValidationError> errors) {
		if (reactivationDto.getReactivationWefDate() != null
				&& reactivationDto.getReactivationWefDate().isBefore(LocalDate.now()))
		addNonEmpty(errors,
				ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
						.referenceId(reactivationDto.getReferenceId() == null ? ""
								: String.valueOf(reactivationDto.getReferenceId()))
						.build());

		groupRepository.findById(reactivationDto.getReferenceId()).ifPresent(group -> {
			if (group.getDeactivate() != null && group.getDeactivate().getDeactivatedDate() == null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(THIS_GROUP_IS_ALREADY_ACTIVE)
								.referenceId(reactivationDto.getReferenceId() == null ? ""
										: String.valueOf(reactivationDto.getReferenceId()))
								.build());
		});
	}

	@Override
	public void isApprovalPending(Boolean submittedForApproval) {
		if (submittedForApproval != null && submittedForApproval.equals(Boolean.TRUE))
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR,
					REQUEST_IS_ALREADY_SUBMITTED_FOR_APPROVAL_YOU_CAN_T_UPDATE_ANYTHING);
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}

}
