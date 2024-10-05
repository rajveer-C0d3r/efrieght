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
public class UserRoleCreationRequest implements Serializable{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 6520353017754276849L;

	/** Id of a Role object */
	private Long roleId;

}
