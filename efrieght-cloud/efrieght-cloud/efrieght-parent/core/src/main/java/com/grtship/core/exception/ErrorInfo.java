package com.grtship.core.exception;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo implements Serializable{
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILURE = "failure";
	
	private static final long serialVersionUID = -7653054241707064829L;
	private String status;
	private String statusCode;
	private List<FieldMessage> fieldMessage;
	private String message;
	private List<Object> list;
	private Object obj;

}
