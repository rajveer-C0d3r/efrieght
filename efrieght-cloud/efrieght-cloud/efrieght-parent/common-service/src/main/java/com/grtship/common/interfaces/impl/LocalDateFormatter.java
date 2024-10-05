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
public class LocalDateFormatter implements TimeStampConverter {

	private static final Logger log = LoggerFactory.getLogger(LocalDateFormatter.class);

	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean isAssignableFrom(Class clazz) {
		log.info("input for isAssignableFrom for localdate type is  : {} ", clazz.getSimpleName());
		return java.time.LocalDate.class.isAssignableFrom(clazz);
	}

	@Override
	public String getName() {
		log.info("formatter is of type Local Date object ");
		return "LocalDateFormatter";
	}

	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject) {
		if (!ObjectUtils.isEmpty(fieldValue) && !StringUtils.isEmpty(fieldName)) {
			try {
				jsonObject.put(fieldName, DateTimeFormatter.ofPattern(TimeStampFormatType.LOCALDATE.getLabel())
						.withZone(ZoneId.systemDefault()).format((TemporalAccessor) fieldValue));
			} catch (JSONException e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		}
	}

}
