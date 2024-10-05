package com.grtship.core.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
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
public class CsaDetailsDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;
	@NotBlank(message = "Name is mandatory.")
	@NotNull(message = "Name is mandatory.")
	private String name;
	@NotBlank(message = "Email is mandatory.")
	@NotNull(message = "Email is mandatory.")
	private String email;
	@NotBlank(message = "Contact number is mandatory.")
	@NotNull(message = "Contact number is mandatory.")
	private String contactNo;
	private String langKey;
	
	@EnableAuditLevel(idOnly = true)
	@NotEmpty(message = "User roles are mandatory.")
	@NotNull(message = "User roles are mandatory.")
	private List<UserRoleDTO> userRoles;
	private Long clientId;
	private Long branchId;
	private Long companyId;

}
