package com.grtship.account.exception;

import com.grtship.core.constant.ErrorCode;

public class InvalidDataException1 extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2324955145787942311L;

	public InvalidDataException1() {
		super(ErrorCode.INVALID_DATA_ERROR);
	}

	public InvalidDataException1(Integer errorCode) {
		super(errorCode);

	}

	public InvalidDataException1(Integer errorCode, String message) {
		super(errorCode, message);
	}

}
