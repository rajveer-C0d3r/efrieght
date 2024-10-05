package com.grtship.common.exception;

import com.grtship.core.constant.ErrorCode;

public class InvalidDataException extends ApplicationException {

	private static final long serialVersionUID = 2324955145787942311L;

	public InvalidDataException() {
		super(ErrorCode.INVALID_DATA_ERROR, "Invalid Data");
	}

	public InvalidDataException(String errorMessage) {
		super(ErrorCode.INVALID_DATA_ERROR,errorMessage);

	}

	public InvalidDataException(Integer errorCode, String message) {
		super(errorCode, message);
	}

}
