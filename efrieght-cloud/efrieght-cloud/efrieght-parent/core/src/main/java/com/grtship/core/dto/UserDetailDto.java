/**
 * 
 */
package com.grtship.core.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize
@EnableCustomAudit
public class UserDetailDto {
	private String email;
	private String commmaSeperatedAuthority;
}
