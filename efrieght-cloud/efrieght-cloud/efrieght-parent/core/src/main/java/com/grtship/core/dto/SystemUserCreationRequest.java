package com.grtship.core.dto;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.enumeration.UserType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EnableCustomAudit
public class SystemUserCreationRequest {
	
	private Long id;
	@NotBlank private String name;
	@Email private String email;
	private String contactNo;
	private Boolean deactivate;
	private Long designationId;
	private Long departmentId;
	@NotNull private UserType userType;
	private String langKey;
	private Set<UserRoleCreationRequest> userRoles;

}
