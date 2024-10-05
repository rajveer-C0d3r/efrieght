package com.grtship.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private HttpStatus errorcode;

	private String description;

	public ServiceException(String description, Exception e, HttpStatus errorcode) {
		super(description, e);
		this.description = description;
		this.errorcode = errorcode;
	}

	public ServiceException(HttpStatus errorcode, String description) {
		super(description);
		this.description = description;
		this.errorcode = errorcode;
	}

	public ServiceException(String description) {
		super(description);
	}

}
