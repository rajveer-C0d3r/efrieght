/**
 * 
 */
package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditDataWrapper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AuditDataHeader header;
	private Map<String, Object> payload;
}
