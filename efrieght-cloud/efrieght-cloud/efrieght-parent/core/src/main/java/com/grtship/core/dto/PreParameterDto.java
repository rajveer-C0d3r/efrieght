/**
 * 
 */
package com.grtship.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EnableCustomAudit
public class PreParameterDto {
	private Long id;
	private String referenceType;
	private String action;
	private String owner;
	private Long sequence;
}
