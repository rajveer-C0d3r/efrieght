package com.grtship.core.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class CompanyBranchCreationDTO extends AbstractAuditingDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String code;

	private String name;

	private AddressDTO address;

	@NotNull(message = "Client Id can't be null.")
	private Long clientId;

	@NotNull(message = "Company Id can't be null.")
	private Long companyId;
	
	@EnableAuditLevel(idOnly = true)
	private Set<BranchGstDetailsDTO> branchGstDetails;

	private DomainStatus status;
	
	private Boolean submittedForApproval;

}
