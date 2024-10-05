package com.grtship.core.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * A DTO for the {@link com.grtship.efreight.client.domain.Client} entity.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class ClientDTO extends AbstractAuditingDTO {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 5880350585624933739L;

	private Long id;

	@Size(max = 10, message = "Maximum size of client code is 10 characters.")
	private String code;

	@NotNull(message = "Client Name Is Mandatory, Please Enter Client Name.")
    @NotBlank(message = "Client Name Is Mandatory, Please Enter Client Name.")
    @Size(max = 60, message = "Maximum size of client name is 60 characters.")
	private String name;

	@Size(max = 10, message = "Maximum size of client mobile no is 10 characters.")
	@NotNull(message = "Mobile Number Is Mandatory, Please Enter Mobile Number.")
    @NotBlank(message = "Mobile Number Is Mandatory, Please Enter Mobile Number.")
	private String mobileNo;

	@Size(max = 30, message = "Maximum size of client email id is 30 characters.")
	@Email(message = "Enter Valid Email Address")
    @NotNull(message = "Email Id Is Mandatory, Please Enter Email Id.")
    @NotBlank(message = "Email Id Is Mandatory, Please Enter Email Id.")
	private String emailId;

	@NotNull(message = "PAN No Or Income Tax Id Is Mandatory, Please Enter PAN No Or Income Tax Id.")
    @NotBlank(message = "PAN No Or Income Tax Id Is Mandatory, Please Enter PAN No Or Income Tax Id.")
    @Size(max = 16, message = "Maximum size of pan no is 16 characters.")
	private String panNo;

	@Size(max = 16, message = "Maximum size of sales tax id is 16 characters.")
	private String salesTaxId;

	private Boolean activeFlag = false;
	
	private Boolean submittedForApproval = false;
	
	private DomainStatus status;
	
	@EnableAuditLevel(idOnly = true)
	@NotEmpty(message = "GSA User Details Are Mandatory, Please Enter GSA User Details.")
	@NotNull(message = "GSA User Details Are Mandatory, Please Enter GSA User Details.")
	private List<GsaDetailsDTO> gsaDetails;
	
	@EnableAuditLevel(level = 1)
	@NotNull(message = "Address Details Are Mandatory, Please Enter Address Details.")
	private AddressDTO address;
	
	@EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
	
	@EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;

}
