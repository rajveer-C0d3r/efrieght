package com.grtship.account.exception;

import com.grtship.core.constant.ErrorCode;

public class ApplicationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3591869389298567256L;
	private Integer errorCode = ErrorCode.UNKNOWN;

	public ApplicationException(Integer errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ApplicationException(Integer errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

}
