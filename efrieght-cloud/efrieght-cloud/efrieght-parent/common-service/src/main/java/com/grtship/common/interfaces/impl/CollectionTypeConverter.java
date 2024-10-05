package com.grtship.common.interfaces.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.JsonConverter;
import com.grtship.common.interfaces.ObjectTypeConverter;
import com.grtship.core.annotation.EnableAuditLevel;

@Component
public class CollectionTypeConverter implements ObjectTypeConverter {

	private static final Logger log = LoggerFactory.getLogger(CollectionTypeConverter.class);

	@Override
	public String getName() {
		log.error("CollectionTypeConverter class name called  : {} ", "CollectionTypeConverter");
		return "CollectionTypeConverter";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isAssignable(Class clazz) {
		log.error("CollectionTypeConverter class isAssignable method called : {} ", clazz);
		return java.util.Collection.class.isAssignableFrom(clazz);
	}

	private void processIdOnly(Object fieldValue, String fieldName, JSONObject jsonObject) {
		try {
			jsonObject.put(fieldName,
					extractId(fieldValue).stream().map(Object::toString).collect(Collectors.joining(",")));
		} catch (JSONException e) {
			log.error("error occured while json convertion : {} ", e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Long> extractId(Object fieldValue) {
		Collection fieldValueCollection = (Collection) fieldValue;
		List<Long> ids = new ArrayList<>(fieldValueCollection.size());
		fieldValueCollection.stream().forEach(obj -> {
			ids.add(extractIdFromObject(obj));
		});
		return ids;
	}

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
	public void convert(Object fieldValue, String fieldName, EnableAuditLevel annotation, JSONObject jsonObject,
			JsonConverter jsonConvertor) {
		int level = 0;
		if (annotation != null) {
			boolean readIdOnly = annotation.idOnly();
			if (readIdOnly) {
				processIdOnly(fieldValue, fieldName, jsonObject);
				return;
			}

			level = annotation.level();
			if (level > 0) {
				JSONArray arr = new JSONArray();
				try {
					jsonObject.put(fieldName, arr);
				} catch (JSONException e1) {
					log.error("error occured while json convertion : {} ", e1.getMessage());
				}

				Collection fieldValueCollection = (Collection) fieldValue;
				fieldValueCollection.stream().forEach(obj -> {
					try {
						JSONObject jsonObject1 = new JSONObject();
						JSONObject convert = jsonConvertor.convert(obj, jsonObject1);
						arr.put(convert);
					} catch (Exception e) {
						log.error("error occured while json convertion : {} ", e.getMessage());
					}
				});

			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject, JsonConverter jsonConvertor) {
		JSONArray arr = new JSONArray();
		try {
			jsonObject.put(fieldName, arr);
		} catch (JSONException e1) {
			log.error("error occured while json convertion : {} ", e1.getMessage());
		}
		Collection fieldValueCollection = (Collection) fieldValue;
		fieldValueCollection.stream().forEach(obj -> {
			try {
				JSONObject jsonObject1 = new JSONObject();
				JSONObject convert = jsonConvertor.convert(obj, jsonObject1);
				arr.put(convert);
			} catch (Exception e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		});
	}
}
