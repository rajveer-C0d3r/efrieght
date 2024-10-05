package com.grtship.core.dto;

import java.io.Serializable;
import java.util.Set;

import com.grtship.core.annotation.EnableCustomAudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EnableCustomAudit
public class UserCompanyCreationRequestDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6720414996741159287L;
	private Long id;
	/** Id of Company Object */
	private Long companyId;
	private Long branchId;
	private Boolean allBranches;
	private Set<UserRoleCreationRequest> userRoles;
	
	
	
	

}
