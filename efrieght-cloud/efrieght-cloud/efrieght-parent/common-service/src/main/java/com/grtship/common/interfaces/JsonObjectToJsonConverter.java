package com.grtship.common.interfaces;

import org.json.JSONObject;

public interface JsonObjectToJsonConverter {
	JSONObject convert(final Object data) throws IllegalArgumentException, IllegalAccessException;

	JSONObject convert(final Object data, JSONObject jsonObject) throws IllegalArgumentException, IllegalAccessException;
}
