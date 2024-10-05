/**
 * 
 */
package com.grtship.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Ajay
 *
 */
public class BadCredentialException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public BadCredentialException(String message) {
		super(message);
	}

	public BadCredentialException(String message, Throwable t) {
		super(message, t);
	}

}
