package com.grtship.common.exception;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Integer errorcode;

	private String message;
	
	private List<String> messages;

	public ApplicationException(Integer errorcode, String message) {
		super();
		this.errorcode = errorcode;
		this.message = message;
		
	}
	public ApplicationException(Integer errorcode, List<String> messages) {
		super();
		this.errorcode = errorcode;
		this.messages = messages;
		
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Integer errorCode) {
		this.errorcode = errorCode;
	}

}
