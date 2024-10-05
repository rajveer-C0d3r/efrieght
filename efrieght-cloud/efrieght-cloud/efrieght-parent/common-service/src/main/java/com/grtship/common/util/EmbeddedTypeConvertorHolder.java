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

import com.grtship.common.interfaces.EmbeddedTypeConvertor;

/**
 * @author hp
 *
 */
@Component
public class EmbeddedTypeConvertorHolder {

	private final Map<String, EmbeddedTypeConvertor> embeddedType;

	public EmbeddedTypeConvertorHolder() {
		embeddedType = new HashMap<>();
	}

	@Autowired
	private List<EmbeddedTypeConvertor> embeddedTypeConvertors;

	public EmbeddedTypeConvertor getConverter(@SuppressWarnings("rawtypes") Class clazz) {
		Optional<EmbeddedTypeConvertor> embeddedTypeConvertor = embeddedTypeConvertors.stream()
				.filter(obj -> obj.isEmbedded(clazz)).findFirst();
		return (embeddedTypeConvertor.isPresent() ? embeddedTypeConvertor.get() : null);
	}

	public void addConverter(String className, EmbeddedTypeConvertor obj) {
		this.embeddedType.put(className, obj);
	}

	@PostConstruct
	public void register() {
		if (null == embeddedTypeConvertors || embeddedTypeConvertors.isEmpty()) {
			return;
		}
		embeddedTypeConvertors.forEach(obj -> addConverter(obj.getName(), obj));
	}

}
