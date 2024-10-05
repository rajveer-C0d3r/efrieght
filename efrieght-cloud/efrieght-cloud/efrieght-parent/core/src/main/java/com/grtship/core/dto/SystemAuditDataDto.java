/**
 * 
 */
package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.Data;

/**
 * @author hp
 *
 */
@Data
@EnableCustomAudit
public class SystemAuditDataDto {
	private Long id;
	private String propertyName;
	private String oldValue;
	private String newValue;
}
