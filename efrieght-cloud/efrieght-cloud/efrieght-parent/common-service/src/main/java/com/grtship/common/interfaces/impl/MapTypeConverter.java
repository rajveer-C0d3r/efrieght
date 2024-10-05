/**
 * 
 */
package com.grtship.common.interfaces.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.JsonConverter;
import com.grtship.common.interfaces.ObjectTypeConverter;
import com.grtship.core.annotation.EnableAuditLevel;

/**
 * @author Ajay
 *
 */
@Component
public class MapTypeConverter implements ObjectTypeConverter {

	private static final Logger log = LoggerFactory.getLogger(MapTypeConverter.class);

	@Override
	public String getName() {
		log.error("MapTypeConverterImpl class name called  : {} ", "MapTypeConverter");
		return "MapTypeConverter";
	}

	@Override
	public boolean isAssignable(@SuppressWarnings("rawtypes") Class className) {
		log.error("CollectionTypeConverter class isAssignable method called : {} ", className);
		return java.util.Map.class.isAssignableFrom(className);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void convert(Object fieldValue, String fieldName, EnableAuditLevel annotation, JSONObject jsonObject,
			JsonConverter jsonConvertor) {
		int level = 0;
		if (annotation != null) {
			boolean readIdOnly = annotation.idOnly();
			if (readIdOnly) {
				processIdOnly(fieldValue, fieldName, jsonObject);
			}

			level = annotation.level();
			if (level > 0) {
				Map fieldValueMap = (Map) fieldValue;
				fieldValueMap.keySet().stream().limit(level).forEach(key -> {
					try {
						JSONObject jsonMap = new JSONObject();
						JSONObject convert = jsonConvertor.convert(fieldValueMap.get(key), jsonMap);
						jsonObject.put(fieldName, convert);
					} catch (Exception e) {
						log.error("error occured while json convertion : {} ", e.getMessage());
					}
				});

			}
		}

	}

	private void processIdOnly(Object fieldValue, String fieldName, JSONObject jsonObject) {
		try {
			jsonObject.put(fieldName,
					extractId(fieldValue).stream().map(Object::toString).collect(Collectors.joining(",")));
		} catch (JSONException e) {
			log.error("error occured while json convertion : {} ", e.getMessage());
		}
	}

	/**
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Long> extractId(Object fieldValue) {
		Map fieldValueMap = (Map) fieldValue;
		List<Long> ids = new ArrayList<>(fieldValueMap.size());
		fieldValueMap.keySet().stream().forEach(obj -> ids.add(extractIdFromObject(obj)));
		return ids;
	}

	/**
	 * @param obj
	 * @return
	 */
	private Long extractIdFromObject(Object obj) {
		Long id = 0L;
		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(Boolean.TRUE);
			if (field.isAnnotationPresent(Id.class)) {
				try {
					id = (Long) field.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("error occured while json convertion : {} ", e.getMessage());
				}
			}
		}
		return id;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject, JsonConverter jsonConvertor) {
		Map fieldValueMap = (Map) fieldValue;
		fieldValueMap.keySet().stream().forEach(key -> {
			try {
				JSONObject jsonMap = new JSONObject();
				JSONObject convert = jsonConvertor.convert(fieldValueMap.get(key), jsonMap);
				jsonObject.put(fieldName, convert);
			} catch (Exception e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		});
	}

}
