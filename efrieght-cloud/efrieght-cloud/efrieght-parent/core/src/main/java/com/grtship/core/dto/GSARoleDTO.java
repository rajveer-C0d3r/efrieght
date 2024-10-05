package com.grtship.core.dto;

import java.io.Serializable;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class GSARoleDTO implements Serializable{
	
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -2567107415289609942L;
	private Long roleId;
	private String roleName;

}
