/**
 * 
 */
package com.grtship.common.interfaces.impl;

import java.text.SimpleDateFormat;

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
public class DateFormatter implements TimeStampConverter {

	SimpleDateFormat dateFormat = new SimpleDateFormat(TimeStampFormatType.DATE.getLabel());

	private static final Logger log = LoggerFactory.getLogger(DateFormatter.class);

	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean isAssignableFrom(Class clazz) {
		log.info("input for isAssignableFrom for date type is  : {} ", clazz.getSimpleName());
		return java.util.Date.class.isAssignableFrom(clazz);
	}

	@Override
	public String getName() {
		log.info("formatter is of type date object ");
		return "DateFormatter";
	}

	@Override
	public void convert(Object fieldValue, String fieldName, JSONObject jsonObject) {
		if (!ObjectUtils.isEmpty(fieldValue) && !StringUtils.isEmpty(fieldName)) {
			try {
				jsonObject.put(fieldName, dateFormat.format(fieldValue));
			} catch (JSONException e) {
				log.error("error occured while json convertion : {} ", e.getMessage());
			}
		}
	}

}
