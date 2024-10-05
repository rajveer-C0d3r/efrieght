package com.grtship.mdm.validator.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.common.interfaces.Validator;
import com.grtship.common.interfaces.impl.ObjectValidationError;
import com.grtship.core.constant.ErrorCode;
import com.grtship.core.dto.DocumentDTO;
import com.grtship.core.enumeration.ValidationErrorType;
import com.grtship.mdm.domain.Document;
import com.grtship.mdm.mapper.DocumentMapper;
import com.grtship.mdm.repository.DocumentRepository;

@Component
public class DocumentValidator implements Validator<DocumentDTO> {

	private static final String DOCUMENT_NAME_ALREADY_EXIST = "Document Name already exist";
	private static final String DOCUMENT_CODE_ALREADY_EXIST = "Document code already exist";
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DocumentMapper documentMapper;
	
	@Override
	public List<ValidationError> validate(DocumentDTO documentDTO, String action) {
		List<ValidationError> errors=new LinkedList<>();
		if(action.equals("save") || action.equals("update")) {
			validate(documentMapper.toEntity(documentDTO), errors);
		}
		return errors;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addNonEmpty(List list,Object o) {
		if(null!=o) {
			list.add(o);
		}
	}

	public void validate(Document document,List<ValidationError> errors) {
		addNonEmpty(errors,validateName(document));
		addNonEmpty(errors, validateCode(document));
	}

	private ValidationError validateCode(Document document) {
		if (document.getId() == null
				&& documentRepository.existsByCodeAndCompanyId(document.getCode(), document.getCompanyId())) {
			return returnCodeValidationException(document);
		}
		if (document.getId() != null && documentRepository.existsByCodeAndCompanyIdAndIdIsNot(document.getCode(),
				document.getCompanyId(), document.getId())) {
			return returnCodeValidationException(document);
		}
		return null;

	}

	private ValidationError returnCodeValidationException(Document document) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(DOCUMENT_CODE_ALREADY_EXIST)
				.referenceId(document.getId()==null?"":String.valueOf(document.getId()))
				.build();
	}

	private ValidationError validateName(Document document) {
		if (document.getId() == null
				&& documentRepository.existsByNameAndCompanyId(document.getName(), document.getCompanyId())) {
			return returnNameValidationException(document);
		}
		if (document.getId() != null && documentRepository.existsByNameAndCompanyIdAndIdIsNot(document.getName(),
				document.getCompanyId(), document.getId())) {
			return returnNameValidationException(document);
		}
		return null;

	}

	private ValidationError returnNameValidationException(Document document) {
		return ObjectValidationError.builder()
				.type(ValidationErrorType.ERROR)
				.errorCode(ErrorCode.INVALID_DATA_ERROR)
				.message(DOCUMENT_NAME_ALREADY_EXIST)
				.referenceId(document.getId()==null?"":String.valueOf(document.getId()))
				.build();
	}

}
