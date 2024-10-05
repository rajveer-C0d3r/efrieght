package com.grtship.mdm.validator;

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

