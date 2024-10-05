package com.grtship.core.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class UserRoleDTO implements Serializable{
	
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 6343872103128685481L;
	
	/** Id of UserCompany Class */
	
	private Long userCompanyId;
	@NotNull private Long roleId;
	private String roleName;
	private Long branchId;
	
	public UserRoleDTO(Long userCompanyId, Long roleId, Long branchId){
		this.userCompanyId = userCompanyId;
		this.roleId = roleId;
		this.branchId = branchId;
	}
}
