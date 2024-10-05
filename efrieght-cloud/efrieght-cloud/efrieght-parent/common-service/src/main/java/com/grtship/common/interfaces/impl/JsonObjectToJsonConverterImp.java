package com.grtship.common.interfaces.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.grtship.common.interfaces.JsonObjectToJsonConverter;

@Service
public class JsonObjectToJsonConverterImp implements JsonObjectToJsonConverter {

	@Override
	public JSONObject convert(Object data) throws IllegalArgumentException, IllegalAccessException {
		JSONObject jsonObject=new JSONObject();
		convert(data, jsonObject);
		return null;
	}

	@Override
	public JSONObject convert(Object data, JSONObject jsonObject) throws IllegalArgumentException, IllegalAccessException {
		return null;
	}

}
