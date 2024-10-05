/**
 * 
 */
package com.grtship.common.interfaces.impl;

import java.lang.reflect.Field;

import javax.persistence.Embedded;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.grtship.common.interfaces.EmbeddedTypeConvertor;
import com.grtship.common.interfaces.JsonConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hp
 *
 */
@Slf4j
@Component
public class EmbeddedTypeObjectConvertorImpl implements EmbeddedTypeConvertor {

	@Override
	public String getName() {
		log.info("formatter is of type Embedded object ");
		return "EmbeddedFormatter";
	}

	@Override
	public boolean isEmbedded(@SuppressWarnings("rawtypes") Class field) {
		boolean status = false;
		if (ObjectUtils.isNotEmpty(field)) {
			if (field.isInstance(Object.class)) {
				status = true;
			}
		}
		return status;
	}

	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject, JsonConverter jsonConvertor) {
		if (!ObjectUtils.isEmpty(fieldValue) && !StringUtils.isEmpty(fieldName)) {
			log.info("embeded field value data {} ",fieldValue);
			log.info("embeded field name value {} ",fieldName);
			log.info("respective json object data before added embedded data added {}" ,jsonObject);
			try {
				JSONObject nestedJson = new JSONObject();
				jsonObject.put(fieldName, jsonConvertor.convert(fieldValue, nestedJson));
				}
//				jsonObject.put(fieldName, new Gson().toJson(fieldValue));
			catch (JSONException | IllegalArgumentException | IllegalAccessException e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		}
	}

}
