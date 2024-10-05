package com.grtship.mdm.validator;

public interface ValidationError {
	ValidationErrorType getType();
	String getMessage();
	String getReferenceId();
	Integer getErrorCode();
}
