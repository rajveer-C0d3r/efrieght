/**
 * 
 */
package com.grtship.common.interfaces;

import org.json.JSONObject;

/**
 * @author Ajay
 *
 */
public interface TimeStampConverter {
	@SuppressWarnings("rawtypes")
	boolean isAssignableFrom(Class clazz);

	String getName();
	
	void convert(Object fieldValue, String fieldName, JSONObject jsonObject);
}
