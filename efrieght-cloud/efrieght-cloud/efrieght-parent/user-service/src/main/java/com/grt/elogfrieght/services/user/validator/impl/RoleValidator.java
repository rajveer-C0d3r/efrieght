package com.grt.elogfrieght.services.user.validator.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grt.elogfrieght.services.user.domain.Role;
import com.grt.elogfrieght.services.user.mapper.RoleMapper;
import com.grt.elogfrieght.services.user.repository.RoleRepository;
import com.grt.elogfrieght.services.user.serviceimpl.UserQueryServiceImpl;
import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DeactivationDTO;
import com.grtship.core.dto.ReactivationDTO;
import com.grtship.core.dto.RoleDTO;
import com.grtship.core.enumeration.ValidationErrorType;

import javassist.tools.rmi.ObjectNotFoundException;

@Component
public class RoleValidator implements Validator<Object> {
	private static final String PERMISSIONS_ARE_MANDATORY = "Permissions are mandatory";
	private static final String THIS_ROLE_IS_ALREADY_ACTIVE = "This Role is Already Active";
	private static final String ROLE_NAME_ALREADY_EXIST = "Role Name already exist";
	private static final String ROLE_IS_ALREADY_DEACTIVATED = "Role is Already Deactivated.";
	private static final String DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Deactivation WEF Date can't be past Date.";
	private static final String REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE = "Reactivation WEF Date can't be past Date.";
	private static final String ROLE_NOT_FOUND = "Role Not found.";
	
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserQueryServiceImpl userQueryService;
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public List<ValidationError> validate(Object obj, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if (action.equals("save")) {
			RoleDTO roleDTO=(RoleDTO) obj;
			saveValidation(roleMapper.toEntity(roleDTO), errors);
		}
		if (action.equals("update")) {
			RoleDTO roleDTO=(RoleDTO) obj;
			try {
				updateValidation(roleMapper.toEntity(roleDTO),errors);
			} catch (InvalidDataException | ObjectNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (action.equals("deactivate")) {
			DeactivationDTO deactivationDTO=(DeactivationDTO) obj;
			deactivateValidation(deactivationDTO,errors);
		}
		if (action.equals("reactivate")) {
			ReactivationDTO reactivationDTO=(ReactivationDTO) obj;
			reactivateValidations(reactivationDTO,errors);
		}
		return errors;
	}


	public void saveValidation(Role role,List<ValidationError> errors) throws InvalidDataException {
		addNonEmpty(errors,validatePermission(role));
		addNonEmpty(errors,validateName(role));
	}


	private ValidationError validateName(Role role) {
		if (role.getIsPublic().equals(Boolean.FALSE)
				&& roleRepository.existsByNameAndClientId(role.getName(), role.getClientId())) {
			return returnNameValidationError(role);
		}	
		if (role.getIsPublic().equals(Boolean.FALSE)
				&& roleRepository.existsByNameAndCompanyId(role.getName(), role.getCompanyId())) {
			return returnNameValidationError(role);
		}
		if (role.getIsPublic().equals(Boolean.FALSE) && roleRepository.existsByNameAndIsPublicTrue(role.getName())) {
			return returnNameValidationError(role);
		}
		if (role.getIsPublic().equals(Boolean.TRUE) && roleRepository.existsByName(role.getName())) {
			return returnNameValidationError(role);
		}
		return null;
	}

	private ValidationError returnNameValidationError(Role role) {
		return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ROLE_NAME_ALREADY_EXIST)
				.referenceId(role.getId() == null ? "" : String.valueOf(role.getId())).build();
	}


	private ValidationError validatePermission(Role role) {
		if (CollectionUtils.isEmpty(role.getPermissions())) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(PERMISSIONS_ARE_MANDATORY)
					.referenceId(role.getId() == null ? "" : String.valueOf(role.getId())).build();
		}
		return null;
	}

	public void updateValidation(Role role, List<ValidationError> errors)
			throws ObjectNotFoundException, InvalidDataException {
		if (!roleRepository.findById(role.getId()).isPresent()) {
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ROLE_NOT_FOUND)
							.referenceId(role.getId() == null ? "" : String.valueOf(role.getId())).build());
		} else {
			addNonEmpty(errors, validateUpdatedName(role));
		}
	}


	private ValidationError validateUpdatedName(Role role) {
		if (role.getIsPublic().equals(Boolean.FALSE)
				&& roleRepository.existsByNameAndIsPublicTrueAndIdNot(role.getName(), role.getId())) {
			return returnNameValidationError(role);
		}
		validatePermission(role);
		if (role.getIsPublic().equals(Boolean.FALSE)
				&& roleRepository.existsByNameAndClientIdAndIdNot(role.getName(), role.getClientId(), role.getId())) {
			return returnNameValidationError(role);
		}
		if (role.getIsPublic().equals(Boolean.FALSE)
				&& roleRepository.existsByNameAndCompanyIdAndIdNot(role.getName(), role.getCompanyId(), role.getId())) {
			return returnNameValidationError(role);
		}
		if (role.getIsPublic().equals(Boolean.TRUE)
				&& roleRepository.existsByNameAndIdNot(role.getName(), role.getId())) {
			return returnNameValidationError(role);
		}
		return null;
	}

	public void deactivateValidation(Role role) throws InvalidDataException {
		if (userQueryService.existsByRoleId(role.getId())) {
			throw new InvalidDataException(ErrorCode.INVALID_DATA_ERROR, "This role is already assigned to user");
		}
	}
	
	public void deactivateValidation(@Valid DeactivationDTO deactivateDto, List<ValidationError> errors) {
		if (deactivateDto.getDeactivationWefDate() != null
				&& deactivateDto.getDeactivationWefDate().isBefore(LocalDate.now()))
			addNonEmpty(errors,
					ObjectValidationError.builder().type(ValidationErrorType.ERROR)
							.errorCode(ErrorCode.INVALID_DATA_ERROR).message(DEACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
							.referenceId(deactivateDto.getReferenceId() == null ? ""
									: String.valueOf(deactivateDto.getReferenceId()))
							.build());

		roleRepository.findById(deactivateDto.getReferenceId()).ifPresent(role -> {
			if (role.getDeactivate() != null && role.getDeactivate().getDeactivatedDate() != null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(ROLE_IS_ALREADY_DEACTIVATED)
								.referenceId(deactivateDto.getReferenceId() == null ? ""
										: String.valueOf(deactivateDto.getReferenceId()))
								.build());
		});
	}

	public void reactivateValidations(ReactivationDTO reactivationDto, List<ValidationError> errors) {
		
		if (reactivationDto.getReactivationWefDate() != null
				&& reactivationDto.getReactivationWefDate().isBefore(LocalDate.now()))
		addNonEmpty(errors,
				ObjectValidationError.builder().type(ValidationErrorType.ERROR).errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(REACTIVATION_WEF_DATE_CAN_T_BE_PAST_DATE)
						.referenceId(reactivationDto.getReferenceId() == null ? ""
								: String.valueOf(reactivationDto.getReferenceId()))
						.build());

		roleRepository.findById(reactivationDto.getReferenceId()).ifPresent(role -> {
			if (role.getDeactivate() != null && role.getDeactivate().getDeactivatedDate() == null)
				addNonEmpty(errors,
						ObjectValidationError.builder().type(ValidationErrorType.ERROR)
								.errorCode(ErrorCode.INVALID_DATA_ERROR).message(THIS_ROLE_IS_ALREADY_ACTIVE)
								.referenceId(reactivationDto.getReferenceId() == null ? ""
										: String.valueOf(reactivationDto.getReferenceId()))
								.build());
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addNonEmpty(List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
