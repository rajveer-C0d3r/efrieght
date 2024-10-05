package com.grtship.core.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EnableCustomAudit
public class UserCreationRequestDTO implements Serializable{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = -2116913669948721167L;
	private Long id;
	
	@JsonProperty(required = true)
	@NotBlank(message = "User name is mandatory")
	private String name;
	
	@JsonProperty(required = true)
	@Email(message = "Enter correct email")
	private String email;
	
	@JsonProperty(required = true)
	@NotBlank(message = "Contact No is mandatory")
	private String contactNo;
	private Long designationId;
	private Long departmentId;
	private Boolean activeFlag;
	
	@JsonProperty(required = true)
	@NotNull private boolean allowLogin;
	@Builder.Default private boolean allCompanies = Boolean.FALSE; 
	private Boolean isDeactivated;
	
	@JsonProperty(required = true)
	@NotNull private UserType userType;
	private String langKey;
	private Long clientId; 
	private Long companyId;
	private Long branchId;
	
	@EnableAuditLevel(level = 1)
	private DeactivationDTO deactivate;
	@EnableAuditLevel(level = 1)
    private ReactivationDTO reactivate;
    private DomainStatus status;
    private Boolean submittedForApproval;
    @EnableAuditLevel(idOnly = true)
	private Set<UserCompanyCreationRequestDTO> userCompanies;
	private Set<UserRoleCreationRequest> userRoles;
	
	
	public void addUserRole(UserRoleCreationRequest userRole) {
		if(userRoles == null) {
			userRoles = new HashSet<>();
		}
		userRoles.add(userRole);
	}
	
	


	


}
