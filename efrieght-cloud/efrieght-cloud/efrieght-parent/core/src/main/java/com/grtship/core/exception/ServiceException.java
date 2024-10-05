package com.grtship.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String description;

	public ServiceException(String description) {
		super();
		this.description = description;
	}

}
