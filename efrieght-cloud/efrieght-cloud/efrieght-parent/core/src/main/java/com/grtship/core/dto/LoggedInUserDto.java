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
 * @author ER Ajay Sharma
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EnableCustomAudit
public class LoggedInUserDto {
	private Long id;
	private String email;
	private Long clientId;
	private Long companyId;
	private Long branchId;
}
