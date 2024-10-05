package com.grtship.common.interfaces;

import com.grtship.core.enumeration.ValidationErrorType;

public interface ValidationError {
	ValidationErrorType getType();
	String getMessage();
	String getReferenceId();
	Integer getErrorCode();
}
