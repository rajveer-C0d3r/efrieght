/**
 * 
 */
package com.grtship.core.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.IgnoreAuditField;

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
public class AuditDataHeader implements Serializable{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	private String referenceType;
	private String action;
	private String owner;
	private Long sequence;
}
