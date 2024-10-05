/**
 * 
 */
package com.grtship.common.interfaces.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.grtship.common.interfaces.TimeStampConverter;
import com.grtship.core.enumeration.TimeStampFormatType;

/**
 * @author Ajay
 *
 */
@Component
public class InstantDateFormatter implements TimeStampConverter {

	private static final Logger log = LoggerFactory.getLogger(InstantDateFormatter.class);

	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean isAssignableFrom(Class clazz) {
		log.info("input for isAssignableFrom for instant type is  : {} ", clazz.getSimpleName());
		return java.time.Instant.class.isAssignableFrom(clazz);
	}

	@Override
	public String getName() {
		log.info("formatter is of type Instant object ");
		return "InstantDateFormatter";
	}

	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject) {
		if (!ObjectUtils.isEmpty(fieldValue) && !StringUtils.isEmpty(fieldName)) {
			try {
				jsonObject.put(fieldName, DateTimeFormatter.ofPattern(TimeStampFormatType.INSTANT.getLabel())
						.withZone(ZoneId.systemDefault()).format((TemporalAccessor) fieldValue));
			} catch (JSONException e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		}
	}

}
