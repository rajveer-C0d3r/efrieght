/**
 * 
 */
package com.grtship.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grtship.common.interfaces.TimeStampConverter;

/**
 * @author Ajay
 *
 */
@Component
public class TimeStampConverterHolder {

	private final Map<String, TimeStampConverter> objectTypes;

	public TimeStampConverterHolder() {
		objectTypes = new HashMap<>();
	}

	@Autowired
	private List<TimeStampConverter> timeStampConverters;

	@SuppressWarnings("rawtypes")
	public TimeStampConverter getConverter(Class clazz) {
		Optional<TimeStampConverter> timeStampConvertor = timeStampConverters.stream()
				.filter(obj -> obj.isAssignableFrom(clazz)).findFirst();
		return (timeStampConvertor.isPresent() ? timeStampConvertor.get() : null);
	}

	public void addConverter(String className, TimeStampConverter obj) {
		this.objectTypes.put(className, obj);
	}

	@PostConstruct
	public void register() {
		if (null == timeStampConverters || timeStampConverters.isEmpty()) {
			return;
		}
		timeStampConverters.forEach(obj -> addConverter(obj.getName(), obj));
	}

}
