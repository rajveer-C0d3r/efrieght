package com.grtship.common.exception.handler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.grtship.common.exception.InvalidDataException;
import com.grtship.common.exception.ValidationException;
import com.grtship.core.exception.ErrorInfo;
import com.grtship.core.exception.FieldMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

	@ExceptionHandler({AccessDeniedException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ErrorInfo handleAccessDeniedExceptions(AccessDeniedException ex) {
		log.debug(String.format("Handling all AccessDeniedException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.FORBIDDEN.value()));
	}
	
	@ExceptionHandler({Exception.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo handleExceptions(Exception ex) {
		
		log.error(String.format("Handling all exception {} : ",ex.getMessage()), ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
	
	@ExceptionHandler({RuntimeException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo handleRuntimeException(RuntimeException ex) {
		log.error(String.format("Handling all RuntimeException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
	
	@ExceptionHandler({DataIntegrityViolationException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorInfo handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		log.debug(String.format("Handling all DataIntegrityViolationException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
	}
	
	@ExceptionHandler({FileNotFoundException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorInfo handleFileNotFoundException(FileNotFoundException ex) {
		log.warn(String.format("Handling all FileNotFoundException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.NOT_FOUND.value()));
	}
	
	@ExceptionHandler({javax.validation.ConstraintViolationException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorInfo handleConstraintViolationException(javax.validation.ConstraintViolationException ex) {
		log.debug(String.format("Handling all ConstraintViolationException exception {} : ",ex.getMessage()),ex);
		List<FieldMessage> violations = new ArrayList<>();
		ex.getConstraintViolations().forEach(err -> 
			violations.add(FieldMessage.builder().fieldName(err.getPropertyPath().toString())
			.message(err.getMessage())
			.value(err.getInvalidValue()).build())
		);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()), violations);
	}
	
	@ExceptionHandler({ConstraintViolationException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorInfo handleConstraintViolationException(ConstraintViolationException ex) {
		log.debug(String.format("Handling all ConstraintViolationException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(ex.getErrorCode()));
	}
	
	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorInfo handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error(String.format("Handling all MethodArgumentNotValidException exception {} : ",ex.getMessage()),ex);
		List<FieldMessage> violations = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			FieldError err = (FieldError) error;
			violations.add(FieldMessage.builder().fieldName(err.getField()).message(err.getDefaultMessage()).build());
		});
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()), violations);
	}
	
	@ExceptionHandler({TransientObjectException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorInfo handleTransientObjectException(TransientObjectException ex) {
		log.error(String.format("Handling all TransientObjectException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}
	
	@ExceptionHandler({InvalidDataException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorInfo handleInvalidDataExceptions(InvalidDataException ex) {
		log.debug(String.format("Handling all InvalidDataException exception {} : ",ex.getMessage()),ex);
		return wrapException(ex.getMessage(), ex.getErrorcode().toString());
	}
	
	@ExceptionHandler({ValidationException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorInfo handleValidationException(ValidationException ex) {
		log.debug(String.format("Handling all Validation exception {} : ",ex.getMessage()),ex);
		List<FieldMessage> violations = new ArrayList<>();
		ex.getErrors().forEach(err -> 
			violations.add(FieldMessage.builder()
			.message(err.getMessage())
			.value(err.getErrorCode()).build())
		);
		return wrapException(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()), violations);
	}
	
	private ErrorInfo wrapException(String exceptionMsg, String statusCode){
		return ErrorInfo.builder().message(exceptionMsg).status(ErrorInfo.STATUS_FAILURE).statusCode(statusCode)
				.build();
	}

	private ErrorInfo wrapException(String exceptionMsg, String statusCode, List<FieldMessage> violations) {
		return ErrorInfo.builder().status(ErrorInfo.STATUS_FAILURE).statusCode(statusCode).fieldMessage(violations)
				.build();
	}
	
}
