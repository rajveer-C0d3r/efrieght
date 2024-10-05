package com.grtship.common.interfaces;


import org.json.JSONObject;

import com.grtship.core.annotation.EnableAuditLevel;



public interface ObjectTypeConverter {

	String getName();

	@SuppressWarnings("rawtypes")
	boolean isAssignable(Class className);

	void convert(Object fieldValue, String fieldName, EnableAuditLevel annotation, JSONObject jsonObject,
			JsonConverter jsonConvertor);

	void convert(Object fieldValue, String fieldName, JSONObject jsonObject, JsonConverter jsonConvertor);

}
