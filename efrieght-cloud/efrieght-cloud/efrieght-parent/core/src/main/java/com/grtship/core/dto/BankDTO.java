package com.grtship.core.dto;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.AccountType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.Bank} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class BankDTO extends ClientAuditableEntityDTO {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L; 

	private Long id;

	@NotNull(message = "Bank Code is Mandatory.")
	private String code;

	@NotNull(message = "Bank Name is Mandatory.")
	private String name;

	@NotNull(message = "Branch Name is Mandatory.")
	private String branchName;

	private String ifscCode;

	@NotNull(message = "Account No is Mandatory.")
	private String accountNo;

	@NotNull(message = "Account Type is Mandatory.")
	private AccountType accountType;

	private String swiftCode;

	@NotNull(message = "Address is Mandatory.")
	private AddressDTO address;

	private String relationshipManager;

	private String emailId;

	private String contactNo;

}
