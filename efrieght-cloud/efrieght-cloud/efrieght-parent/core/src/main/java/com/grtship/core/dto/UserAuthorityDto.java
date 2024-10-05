/**
 * 
 */
package com.grtship.core.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ajay
 *
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EnableCustomAudit
public class UserAuthorityDto {
	List<String> authorities;
	Map<String, List<String>> permission;
}
