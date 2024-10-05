package com.grtship.common.exception;

import java.util.List;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.constant.ErrorCode;

public class ValidationException extends ApplicationException {

	private static final long serialVersionUID = -1406070965652712523L;
	private List<ValidationError> errors;
	
	
	
	public ValidationException() {
		super(ErrorCode.VALIDATION_FAILED, "Validation Failed, Check your data");
	}
	
	public ValidationException(List<ValidationError> errors) {
		this();
		this.errors=errors;
	}

	public ValidationException(Integer errorCode, String message) {
		super(errorCode, message);
	}

	public ValidationException(Integer errorCode) {
		super(errorCode);
	}

	public ValidationException(String message) {
		super(message);
	}

	public List<ValidationError> getErrors() {
		return errors;
	}
}