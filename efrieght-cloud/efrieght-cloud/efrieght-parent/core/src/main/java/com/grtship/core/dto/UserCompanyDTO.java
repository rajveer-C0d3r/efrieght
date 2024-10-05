package com.grtship.core.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.grt.oath2.domain.UserCompany} entity.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EnableCustomAudit
public class UserCompanyDTO implements Serializable {
    
    /**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -4706124817562168126L;

    private Long companyId;
    private String companyName;
    private Long branchId;
    private String branchName;
    private Boolean allBranches;
    
    @EnableAuditLevel(idOnly = true)
    private Set<UserRoleDTO> userRoles;
    

    // Ignoring this field for now
    @JsonIgnore
    private Long userId;
    
    public UserCompanyDTO(Long companyId, Boolean allBranches, Long userId) {
    	this.companyId = companyId;
    	this.allBranches = allBranches;
    	this.userId = userId;
    }
    
    public UserCompanyDTO(Long companyId, String companyName,Boolean allBranches) {
    	this.companyId = companyId;
    	this.allBranches = allBranches;
    	this.companyName = companyName;
    }
    
    public UserCompanyDTO(Long branchId, String branchName, Long companyId, String companyName, Boolean allBranches) {
    	this.companyId = companyId;
    	this.allBranches = allBranches;
    	this.companyName = companyName;
    	this.branchId = branchId;
    	this.branchName = branchName;
    }
    
    public UserCompanyDTO(String branchName, Long companyId, String companyName, Boolean allBranches) {
    	this.companyId = companyId;
    	this.allBranches = allBranches;
    	this.companyName = companyName;
    	this.branchName = branchName;
    }
    
    public void addUserRole(UserRoleDTO userRole) {
    	if(this.userRoles==null) {
    		this.userRoles = new HashSet<>();
    	}
    	this.userRoles.add(userRole);
    }

}
