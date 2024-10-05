package com.grtship.authorisation.validator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.authorisation.domain.ObjectApprovalSequence;
import com.grtship.authorisation.domain.ObjectModule;
import com.grtship.authorisation.mapper.ObjectModuleMapper;
import com.grtship.authorisation.repository.ObjectModuleRepository;
import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.ObjectModuleDTO;
import com.grtship.core.enumeration.ValidationErrorType;

@Component
public class ObjectModuleValidator implements Validator<ObjectModuleDTO> {

	private static final String MODULE_NAME_ALREADY_EXIST = "Module Name with particular Action already exist";
	@Autowired
	private ObjectModuleRepository objectModuleRepository;
	@Autowired
	private ObjectModuleMapper objectModuleMapper;
	
	@Override
	public List<ValidationError> validate(ObjectModuleDTO objectModuleDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();	
		if(action.equals("save")) {
			isValidForSave(objectModuleMapper.toEntity(objectModuleDTO),errors);
		}
		if(action.equals("update")) {
			isValidForUpdate(objectModuleMapper.toEntity(objectModuleDTO),errors);
		}
		return errors;
	}


	public void isValidForSave(ObjectModule objectModule, List<ValidationError> errors) {
		addNonEmpty(errors, validateModuleName(objectModule));
		addNonEmpty(errors, validateApprovalSequence(objectModule));
	}

	private ValidationError validateApprovalSequence(ObjectModule objectModule) {
		if (!objectModule.getApprovalLevel().equals(objectModule.getObjectApprovalSequences().size())) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message("Total " + objectModule.getApprovalLevel() + " Approval Sequences are required")
					.referenceId(objectModule.getId()==null?"":String.valueOf(objectModule.getId()))
					.build();
		}
		Set<Integer> approvalSequenceList = objectModule.getObjectApprovalSequences().stream()
				.map(ObjectApprovalSequence::getApprovalSequence).collect(Collectors.toSet());
		if (approvalSequenceList.size() != objectModule.getApprovalLevel()) {
			return ObjectValidationError.builder()
					.type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR)
					.message("Approval Sequence must be unique in Approval Sequence list ")
					.referenceId(objectModule.getId()==null?"":String.valueOf(objectModule.getId()))
					.build();
		}
		return null;
	}
	
	private ValidationError validateModuleName(ObjectModule objectModule) {
		if (objectModuleRepository
				.findByModuleNameAndActionAndClientIdAndCompanyId(objectModule.getModuleName(),
						objectModule.getAction(), objectModule.getClientId(), objectModule.getCompanyId())
				.isPresent()) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(MODULE_NAME_ALREADY_EXIST)
					.referenceId(objectModule.getId() == null ? "" : String.valueOf(objectModule.getId())).build();
		}
		return null;
	}

	public void isValidForUpdate(ObjectModule objectModule, List<ValidationError> errors) {
		addNonEmpty(errors, validateModuleNameForUpdate(objectModule));
		addNonEmpty(errors, validateApprovalSequence(objectModule));
	}
	
	private ValidationError validateModuleNameForUpdate(ObjectModule objectModule) {
		if (objectModuleRepository.findByModuleNameAndActionAndClientIdAndCompanyIdAndIdNot(
				objectModule.getModuleName(), objectModule.getAction(), objectModule.getClientId(),
				objectModule.getCompanyId(), objectModule.getId()).isPresent()) {
			return ObjectValidationError.builder().type(ValidationErrorType.ERROR)
					.errorCode(ErrorCode.INVALID_DATA_ERROR).message(MODULE_NAME_ALREADY_EXIST)
					.referenceId(objectModule.getId() == null ? "" : String.valueOf(objectModule.getId())).build();
		}
		return null;
	}

	
	@SuppressWarnings({"unchecked", "rawtypes" })
	private void addNonEmpty(List list, Object o) {
		if (null != o) {
			list.add(o);
		}
	}
}
