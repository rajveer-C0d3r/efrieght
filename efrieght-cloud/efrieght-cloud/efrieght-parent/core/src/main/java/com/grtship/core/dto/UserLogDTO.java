/**
 * 
 */
package com.grtship.core.dto;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.UserLogType;

import lombok.Data;

/**
 * @author ER Ajay Sharma
 *
 */
@Data
@EnableCustomAudit
public class UserLogDTO {
	private Long id;
	private Long clientId;
	private Boolean status;
	private UserLogType action;
	private Long userId;
	private String email;
	private String message;
}
