/**
 * 
 */
package com.grtship.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Ajay
 *
 */
public class UserDisabledException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public UserDisabledException(String message) {
		super(message);
	}

	public UserDisabledException(String message, Throwable t) {
		super(message, t);
	}
}
