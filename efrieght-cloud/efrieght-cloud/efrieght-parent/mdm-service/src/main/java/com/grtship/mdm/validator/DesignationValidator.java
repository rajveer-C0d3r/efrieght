package com.grtship.mdm.validator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DesignationDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Designation;
import com.grtship.mdm.repository.DesignationRepository;

@Component
public class DesignationValidator implements Validator<DesignationDTO> {
	private static final String DESIGNATION_NAME_CAN_T_BE_DUPLICATE = "Designation Name can't be duplicate!";
	private static final String DESIGNATION_CODE_CAN_T_BE_DUPLICATE = "Designation Code can't be duplicate!";

	@Autowired
	private DesignationRepository designationRepository;
	
	@Override
	public List<ValidationError> validate(DesignationDTO designationDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			addNonEmpty(errors,codeValidation(designationDTO));
			addNonEmpty(errors,nameValidation(designationDTO));
		}
		return errors;
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}

	public ValidationError codeValidation(DesignationDTO designationDto) {

		if (designationDto.getCode() != null) {
			List<Designation> designations = designationRepository.findByCodeAndClientIdAndCompanyId(
					designationDto.getCode(), designationDto.getClientId(), designationDto.getCompanyId());
			Boolean desigationPresent = designations.stream().filter(obj -> obj.getId() != null
					&& designationDto.getId() != null && !(obj.getId().equals(designationDto.getId()))).map(obj -> obj)
					.findAny().isPresent();
			if ((designationDto.getId() == null && !CollectionUtils.isEmpty(designations))
					|| desigationPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(DESIGNATION_CODE_CAN_T_BE_DUPLICATE)
						.referenceId(designationDto.getId()==null?"":String.valueOf(designationDto.getId()))
						.build();
			}
		}
		return null;
	}
	
	public ValidationError nameValidation(DesignationDTO designationDto) {
		if (designationDto.getName() != null) {
			List<Designation> designations = designationRepository.findByNameAndClientIdAndCompanyId(
					designationDto.getName(), designationDto.getClientId(), designationDto.getCompanyId());
			Boolean desigationPresent = designations.stream().filter(obj -> obj.getId() != null
					&& designationDto.getId() != null && !(obj.getId().equals(designationDto.getId()))).map(obj -> obj)
					.findAny().isPresent();
			if ((designationDto.getId() == null && !CollectionUtils.isEmpty(designations))
					|| desigationPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(DESIGNATION_NAME_CAN_T_BE_DUPLICATE)
						.referenceId(designationDto.getId()==null?"":String.valueOf(designationDto.getId()))
						.build();
			}
		}
		return null;
	}
}