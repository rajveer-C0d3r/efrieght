
/**
 * 
 */
package com.grtship.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Ajay
 *
 */
@Component
public class JsonToMapConvertor {

	private static final Logger log = LoggerFactory.getLogger(JsonToMapConvertor.class);

	/**
	 * @param jsonData
	 * @param parentKey
	 * @return
	 */
	public Map<String, String> initiateJsonConvertion(JSONObject jsonData, String parentKey) {
		Map<String, String> auditFeildMap = new HashMap<>();
		return convertJson(auditFeildMap, jsonData, parentKey);
	}

	/**
	 * @param auditFeildMap
	 * @param jsonData
	 * @param parentKey
	 * @return
	 */
	public Map<String, String> convertJson(Map<String, String> auditFeildMap, JSONObject jsonData, String parentKey) {
		log.info("convertJson method called : {} ", jsonData);
		for (String key : jsonData.keySet()) {
			log.info("json key : {} ", key);
			Object object = jsonData.get(key);
			log.info("json value : {} ", object);
			if (object instanceof JSONObject) {
				extractedFromJsonObject(auditFeildMap, parentKey, key, object);
			} else if (object instanceof JSONArray) {
				extractedFromJsonArray(auditFeildMap, parentKey, key, object);
			} else {
				if (!StringUtils.isEmpty(parentKey)) {
					auditFeildMap.put(parentKey + "." + key, String.valueOf(object));
				} else {
					auditFeildMap.put(key, String.valueOf(object));
				}
			}
		}
		return auditFeildMap;
	}

	/**
	 * @param auditFeildMap
	 * @param parentKey
	 * @param key
	 * @param object
	 */
	private void extractedFromJsonObject(Map<String, String> auditFeildMap, String parentKey, String key,
			Object object) {
		String nestedKey = "";
		log.info("given json is of type json object : {} ", object);
		if (!StringUtils.isEmpty(parentKey)) {
			nestedKey = parentKey + "." + key;
		} else {
			nestedKey = key;
		}
		convertJson(auditFeildMap, (JSONObject) object, nestedKey);
	}

	/**
	 * @param auditFeildMap
	 * @param parentKey
	 * @param key
	 * @param object
	 */
	private void extractedFromJsonArray(Map<String, String> auditFeildMap, String parentKey, String key,
			Object object) {
		int count = 0;
		log.info("given json is of type json array : {} ", object);
		Iterator<Object> iterator = ((JSONArray) object).iterator();
		while (iterator.hasNext()) {
			String nestedKey = "";
			Object nestedArrayObject = iterator.next();
			log.info("iterating json array : {} ", nestedArrayObject);
			count++;
			if (!StringUtils.isEmpty(parentKey)) {
				nestedKey = parentKey + "." + key + "." + count;
			} else {
				nestedKey = key + "." + count;
			}
			convertJson(auditFeildMap, (JSONObject) nestedArrayObject, nestedKey);
		}
	}
}
