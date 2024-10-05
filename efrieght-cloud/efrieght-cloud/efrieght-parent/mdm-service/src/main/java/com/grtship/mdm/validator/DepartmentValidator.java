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
import com.grtship.core.dto.DepartmentDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Department;
import com.grtship.mdm.repository.DepartmentRepository;

@Component
public class DepartmentValidator implements Validator<DepartmentDTO> {

	private static final String DEPARTMENT_NAME_CAN_T_BE_DUPLICATE = "Department Name can't be duplicate!";
	private static final String DEPARTMENT_CODE_CAN_T_BE_DUPLICATE = "Department Code can't be duplicate!";

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Override
	public List<ValidationError> validate(DepartmentDTO departmentDTO, String action) {
		List<ValidationError> errors = new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			addNonEmpty(errors,codeValidation(departmentDTO));
			addNonEmpty(errors,nameValidation(departmentDTO));
		}
		return errors;
	}
	
	@SuppressWarnings("unchecked")
	private void addNonEmpty(@SuppressWarnings("rawtypes") List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}

	public ValidationError codeValidation(DepartmentDTO departmentDto) {

		if (departmentDto.getCode() != null) {
			List<Department> departments = departmentRepository.findByCodeAndClientIdAndCompanyId(
					departmentDto.getCode(), departmentDto.getClientId(), departmentDto.getCompanyId());
			Boolean deptPresent = departments.stream().filter(obj -> obj.getId() != null
					&& departmentDto.getId() != null && !(obj.getId().equals(departmentDto.getId()))).map(obj -> obj)
					.findAny().isPresent();
			if ((departmentDto.getId() == null && !CollectionUtils.isEmpty(departments))
					|| deptPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(DEPARTMENT_CODE_CAN_T_BE_DUPLICATE)
						.referenceId(departmentDto.getId()==null?"":String.valueOf(departmentDto.getId()))
						.build();
			}
		}
		return null;
	}
	
	public ValidationError nameValidation(DepartmentDTO departmentDto) {
		if (departmentDto.getName() != null) {
			List<Department> departments = departmentRepository.findByNameAndClientIdAndCompanyId(
					departmentDto.getName(), departmentDto.getClientId(), departmentDto.getCompanyId());
			Boolean deptPresent = departments.stream().filter(obj -> obj.getId() != null
					&& departmentDto.getId() != null && !(obj.getId().equals(departmentDto.getId()))).map(obj -> obj)
					.findAny().isPresent();
			if ((departmentDto.getId() == null && !CollectionUtils.isEmpty(departments))
					|| deptPresent.equals(Boolean.TRUE)) {
				return ObjectValidationError.builder()
						.type(ValidationErrorType.ERROR)
						.errorCode(ErrorCode.INVALID_DATA_ERROR)
						.message(DEPARTMENT_NAME_CAN_T_BE_DUPLICATE)
						.referenceId(departmentDto.getId()==null?"":String.valueOf(departmentDto.getId()))
						.build();
			}
		}
		return null;
	}
}
