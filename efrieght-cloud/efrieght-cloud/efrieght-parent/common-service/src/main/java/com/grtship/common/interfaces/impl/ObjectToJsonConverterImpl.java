/**
 * 
 */
package com.grtship.common.interfaces.impl;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grtship.common.interfaces.JsonConverter;
import com.grtship.common.interfaces.ObjectTypeConverter;
import com.grtship.common.interfaces.TimeStampConverter;
import com.grtship.common.util.ObjectTypeConverterHolder;
import com.grtship.common.util.TimeStampConverterHolder;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.ClassName;
import com.grtship.core.enumeration.TimeStampName;

/**
 * @author Ajay
 *
 */
@Service
public class ObjectToJsonConverterImpl implements JsonConverter {

	@Autowired
	private ObjectTypeConverterHolder typeConverterHolder;

	@Autowired
	private TimeStampConverterHolder timeStampConverterHolder;

	@Autowired
	JsonConverter jsonInterface;

	private static final Logger log = LoggerFactory.getLogger(ObjectToJsonConverterImpl.class);

	public JSONObject convert(final Object data) throws IllegalArgumentException, IllegalAccessException {
		final JSONObject jsonObject = new JSONObject();
		return convert(data, jsonObject);
	}

	@Override
	public JSONObject convert(Object data, JSONObject jsonObject) throws IllegalAccessException {
		log.info("convert method called : {} ", data);
		if (data == null) {
			log.error("data found null in the request : {} ", data);
			return jsonObject;
		}
		if (data.getClass().isAnnotationPresent(EnableCustomAudit.class)) {
			if (ObjectUtils.isNotEmpty(data.getClass().getSuperclass())) {
				setFieldFromSuperclass(data, jsonObject);
			}
			setFieldFromCurrentClass(data, jsonObject);
		}
		return jsonObject;
	}

	private void setFieldFromCurrentClass(Object data, JSONObject jsonObject) throws IllegalAccessException {
		for (Field field : data.getClass().getDeclaredFields()) {
			field.setAccessible(Boolean.TRUE);
//			Object fieldValue = field.get(data);
			if(!field.isAnnotationPresent(IgnoreAuditField.class)) {
				Object fieldValue = field.get(data);
				if (ObjectUtils.isNotEmpty(fieldValue)) {
//					if (!fieldValue.getClass().isAnnotationPresent(IgnoreAuditField.class)) {
						setValueToJsonObject(jsonObject, field, fieldValue);
//					}
				}
			}
//			if (fieldValue != null) {
//				if (!fieldValue.getClass().isAnnotationPresent(IgnoreAuditField.class)) {
//					setValueToJsonObject(jsonObject, field, fieldValue);
//				}
//			}
		}
	}

	private void setValueToJsonObject(JSONObject jsonObject, Field field, Object fieldValue)
			throws IllegalAccessException {
		log.info("object field is of type : {} ", field.getType());
		if (TimeStampName.getTimeStampNames().contains(field.getType().toString())) {
			convertFeildToTimeStampJson(jsonObject, field, fieldValue);
		} else if (ClassName.getClassTypeNames().contains(field.getType().toString())) {
			log.info("object field is of type wrapper | primitive : {} ", field.getType());
			jsonObject.put(field.getName(), fieldValue);
		} else if (field.getType().isEnum()) {
			jsonObject.put(field.getName(), fieldValue.toString());
		} else if (ObjectUtils.isNotEmpty(typeConverterHolder.getConverter(field.getType()))) {
			convertFeildToCollectionJson(jsonObject, field, fieldValue);
		} else {
			convertFieldToEmbeddedJson(jsonObject, field, fieldValue);
		} 
//		else {
//			convert(fieldValue, jsonObject);
//		}
	}

	private void convertFieldToEmbeddedJson(JSONObject jsonObject, Field field, Object fieldValue) {
//		EmbeddedTypeConvertor embeddedTypeConvertor = embeddedTypeConvertorHolder.getConverter(field.getType());
//		if (embeddedTypeConvertor != null) {
//			embeddedTypeConvertor.convert(fieldValue, field.getName(), jsonObject, jsonInterface);
//		}
		EmbeddedTypeObjectConvertorImpl convertorImpl = new EmbeddedTypeObjectConvertorImpl();
		convertorImpl.convert(fieldValue, field.getName(), jsonObject, jsonInterface);
	}

	private void setFieldFromSuperclass(Object data, JSONObject jsonObject) throws IllegalAccessException {
		if(data.getClass().getSuperclass().isAnnotationPresent(EnableCustomAudit.class)) {
			for (Field field : data.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(Boolean.TRUE);
				if(!field.isAnnotationPresent(IgnoreAuditField.class)) {
					Object fieldValue = field.get(data);
					if (ObjectUtils.isNotEmpty(fieldValue)) {
//						if (!fieldValue.getClass().isAnnotationPresent(IgnoreAuditField.class)) {
							setValueToJsonObject(jsonObject, field, fieldValue);
//						}
					}
				}
//				Object fieldValue = field.get(data);
//				if (fieldValue != null) {
//					if (!fieldValue.getClass().isAnnotationPresent(IgnoreAuditField.class)) {
//						setValueToJsonObject(jsonObject, field, fieldValue);
//					}
//				}
			}
		}
	}

	private void convertFeildToCollectionJson(JSONObject jsonObject, Field field, Object fieldValue) {
		ObjectTypeConverter converter = typeConverterHolder.getConverter(field.getType());
		EnableAuditLevel annotation = null;
		if (!field.isAnnotationPresent(IgnoreAuditField.class)) {
			if (field.isAnnotationPresent(EnableAuditLevel.class) && converter != null) {
				annotation = field.getAnnotation(EnableAuditLevel.class);
				log.info("field.getType() : {} ", field.getType());
				if (annotation != null) {
					converter.convert(fieldValue, field.getName(), annotation, jsonObject, jsonInterface);
				}
			}
			if (!field.isAnnotationPresent(EnableAuditLevel.class) && converter != null) {
				converter.convert(fieldValue, field.getName(), jsonObject, jsonInterface);
			}
		}
	}

	private void convertFeildToTimeStampJson(JSONObject jsonObject, Field field, Object fieldValue) {
		TimeStampConverter timeStampConverter = timeStampConverterHolder.getConverter(field.getType());
		if (timeStampConverter != null) {
			timeStampConverter.convert(fieldValue, field.getName(), jsonObject);
		}
	}

}
