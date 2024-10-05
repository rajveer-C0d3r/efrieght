package com.grtship.mdm.validator.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.GstDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.repository.GstRepository;
import com.grtship.mdm.validator.GstValidator;

@Service
@Transactional(readOnly = true)
public class GstValidatorImpl implements GstValidator,Validator<GstDTO> {

	private static final String HSN_SAC_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_HSN_SAC_CODE = "HSN/SAC code already exists, Please enter another HSN/SAC code.";

	private static final String FOR_CREATING_GST_FIRST_LOG_IN_INTO_BRANCH="For Creating Gst First Log in into Branch level";
	@Autowired
	private GstRepository gstRepository;
	
	@Override
	public List<ValidationError> validate(GstDTO gstDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			saveValidations(gstDTO,errors);
		}
		return errors;
	}

	@Override
	public void saveValidations(GstDTO gstDto,List<ValidationError> errors) {
        addNonEmpty(errors, validateBranchId(gstDto));		
		addNonEmpty(errors, validateHsnSacCode(gstDto));
	}
	
	private ValidationError validateBranchId(GstDTO gstDto) {
		if (ObjectUtils.isEmpty(gstDto.getBranchId())) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message(FOR_CREATING_GST_FIRST_LOG_IN_INTO_BRANCH)
					.referenceId(gstDto.getId()==null?"":String.valueOf(gstDto.getId()))
					.build();
		}
		return null;
	}

	private ValidationError validateHsnSacCode(GstDTO gstDto) {
		if (gstDto.getId() == null) {
			if (gstDto.getHsnSacCode() != null && !CollectionUtils
					.isEmpty(gstRepository.findByHsnSacCodeAndClientIdAndCompanyIdAndBranchId(gstDto.getHsnSacCode(),
							gstDto.getClientId(), gstDto.getCompanyId(), gstDto.getBranchId()))) {
				return returnHsnCodeValidationError(gstDto);
			}
		} else {
			if (gstDto.getHsnSacCode() != null && gstDto.getId() != null && !CollectionUtils.isEmpty(
					gstRepository.findByHsnSacCodeAndClientIdAndCompanyIdAndBranchIdAndIdNot(gstDto.getHsnSacCode(),
							gstDto.getClientId(), gstDto.getCompanyId(), gstDto.getBranchId(), gstDto.getId()))) {
				return returnHsnCodeValidationError(gstDto);
			}
		}
		return null;
	}

	private ValidationError returnHsnCodeValidationError(GstDTO gstDto) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(HSN_SAC_CODE_ALREADY_EXISTS_PLEASE_ENTER_ANOTHER_HSN_SAC_CODE)
				.referenceId(gstDto.getId()==null?"":String.valueOf(gstDto.getId()))
				.build();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addNonEmpty(List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}
}
