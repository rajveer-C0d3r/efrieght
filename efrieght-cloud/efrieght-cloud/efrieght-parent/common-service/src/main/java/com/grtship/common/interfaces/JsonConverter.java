/**
 * 
 */
package com.grtship.common.interfaces;

import org.json.JSONObject;

/**
 * @author Ajay
 *
 */
public interface JsonConverter {
	JSONObject convert(final Object data) throws IllegalArgumentException, IllegalAccessException;

	JSONObject convert(final Object data, JSONObject jsonObject) throws IllegalArgumentException, IllegalAccessException;
}
