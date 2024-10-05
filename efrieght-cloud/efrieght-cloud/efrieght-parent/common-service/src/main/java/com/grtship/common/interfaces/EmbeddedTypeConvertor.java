/**
 * 
 */
package com.grtship.common.interfaces;

import org.json.JSONObject;

/**
 * @author hp
 *
 */
public interface EmbeddedTypeConvertor {

	String getName();

	boolean isEmbedded(@SuppressWarnings("rawtypes") Class className);

	void convert(Object fieldValue, String fieldName, JSONObject jsonObject, JsonConverter jsonConvertor);

}
