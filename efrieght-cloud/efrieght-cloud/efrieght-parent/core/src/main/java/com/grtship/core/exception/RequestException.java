package com.grtship.core.exception;

import org.springframework.validation.BindingResult;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String description;

	private BindingResult bindingResult;

	public RequestException(String description) {
		super();
		this.description = description;
	}

}
