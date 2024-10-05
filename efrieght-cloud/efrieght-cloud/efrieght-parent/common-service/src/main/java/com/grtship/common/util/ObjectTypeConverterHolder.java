package com.grtship.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.ObjectTypeConverter;

@Component
public class ObjectTypeConverterHolder {

	private final Map<String, ObjectTypeConverter> objectTypes;

	@Autowired
	private List<ObjectTypeConverter> typeConverters;

	public ObjectTypeConverterHolder() {
		this.objectTypes = new HashMap<>();
	}

	public ObjectTypeConverter getConverter(String className) {
		return this.objectTypes.get(className);
	}

	@SuppressWarnings("rawtypes")
	public ObjectTypeConverter getConverter(Class clazz) {
		Optional<ObjectTypeConverter> objectTypeConvertor = typeConverters.stream()
				.filter(obj -> obj.isAssignable(clazz)).findFirst();
		return (objectTypeConvertor.isPresent() ? objectTypeConvertor.get() : null);
	}

	public void addConverter(String className, ObjectTypeConverter obj) {
		this.objectTypes.put(className, obj);
	}

	@PostConstruct
	public void register() {
		if (null == typeConverters || typeConverters.isEmpty()) {
			return;
		}
		typeConverters.forEach(obj -> addConverter(obj.getName(), obj));
	}

}
