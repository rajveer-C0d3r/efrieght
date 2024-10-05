/**
 * 
 */
package com.grtship.common.beans;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hp
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomKeyObject {
	private Map<String, String> mappingKeys;
}
