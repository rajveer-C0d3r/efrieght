package com.grtship.common.interfaces.impl;

import com.grtship.common.interfaces.ValidationError;
import com.grtship.core.enumeration.ValidationErrorType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectValidationError implements ValidationError {

	private ValidationErrorType type;
	private String message;
	private String referenceId;
	private Integer errorCode;
}

