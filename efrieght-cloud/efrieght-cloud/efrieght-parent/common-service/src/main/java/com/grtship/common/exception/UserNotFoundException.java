/**
 * 
 */
package com.grtship.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Ajay
 *
 */
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -3591869389298567256L;
	private HttpStatus status;
	private String error;
	public UserNotFoundException(String error) {
		super();
		this.status = HttpStatus.NOT_FOUND;
		this.error = error;
	}
	public UserNotFoundException() {
		super();
	}
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
