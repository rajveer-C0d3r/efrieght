package com.grtship.common.exception;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.grtship.core.constant.ErrorCode;

public class BadRequestAlertException extends ApplicationException {

    private static final long serialVersionUID = 1L;
    private String entityName;
    private String errorKey;
    private URI uri;

    public BadRequestAlertException(URI uri, String entityName, String errorKey) {
        super(ErrorCode.BAD_REQUEST);
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.uri = uri;
    }
    
    public BadRequestAlertException(String entityName, String errorKey) {
        super(ErrorCode.BAD_REQUEST);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public BadRequestAlertException(Integer errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

	public BadRequestAlertException(String errorMsg, String entityName, String errorKey) {
		super(ErrorCode.BAD_REQUEST, errorMsg);
		this.errorKey = errorKey;
		this.entityName = entityName;
	}

	public BadRequestAlertException(URI loginAlreadyUsedType, String errorMsg, String entityName, String errorKey) {
		this(errorMsg, entityName, errorKey);
		this.uri = uri;
	}

	public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}
