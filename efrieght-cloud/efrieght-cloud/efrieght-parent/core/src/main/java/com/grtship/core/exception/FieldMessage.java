package com.grtship.core.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldMessage implements Serializable{

	private static final long serialVersionUID = -4546346592067496029L;

	private String fieldName;
	private String messageCode;
	private String message;
	private Object value;
	
	
	public String getMessage() {
		if(message != null){ return message.toUpperCase();}
		return message;
	}
	public void setMessage(String message) {
		if(message != null){ this.message = message.toUpperCase();}
		else{
		    this.message = message;
		}
	}
}

