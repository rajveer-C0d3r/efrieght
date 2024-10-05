package com.grtship.core.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TechnicalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String description;

	public TechnicalException(String description) {
		super(description);
		this.description = description;
	}

}
