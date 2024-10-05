package com.grtship.core.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@EnableCustomAudit
public class GsaDetailsDTO implements Serializable {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 5733768200995661692L;
	
	private Long id;
	@Size(max = 50, message = "Maximum size of GSA user name is 50 characters.")
	@NotNull(message = "GSA User Name Is Mandatory, Please Enter GSA User Name.")
	@NotBlank(message = "GSA User Name Is Mandatory, Please Enter GSA User Name.")
	private String name;
	
	@Size(max = 30, message = "Maximum size of GSA user email is 30 characters.")
	@NotNull(message = "GSA User Email Is Mandatory, Please Enter GSA User Email.")
	@NotBlank(message = "GSA User Email Is Mandatory, Please Enter GSA User Email.")
	private String email;
	
	@Size(max = 15, message = "Maximum size of GSA user contact no is 15 characters.")
	@NotNull(message = "GSA User Contact No Is Mandatory, Please Enter GSA User Contact No.")
	@NotBlank(message = "GSA User Contact No Is Mandatory, Please Enter GSA User Contact No.")
	private String contactNo;
	
	private String langKey;
	
	@EnableAuditLevel(idOnly = true)
	@NotNull(message = "GSA User Role Is Mandatory, Please Enter GSA User Role.")
	
	private List<UserRoleDTO> userRoles;
	private Long clientId;
}
