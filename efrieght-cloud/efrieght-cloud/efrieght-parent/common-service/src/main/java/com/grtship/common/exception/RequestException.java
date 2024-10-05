package com.grtship.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestException extends Exception {

	private static final long serialVersionUID = 1L;

	private HttpStatus errorcode;

	private String description;

	private BindingResult bindingResult;

	public RequestException(HttpStatus errorcode, String description) {
		super();
		this.errorcode = errorcode;
		this.description = description;
	}

	public RequestException(String description) {
		super(description);
	}
}
