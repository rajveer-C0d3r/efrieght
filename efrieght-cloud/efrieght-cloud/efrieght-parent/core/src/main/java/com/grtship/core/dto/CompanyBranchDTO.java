package com.grtship.core.dto;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grtship.efreight.client.domain.CompanyBranch} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class CompanyBranchDTO extends CompanyBranchCreationDTO {
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 3165596247064583965L;

	private String companyName;
	
	private Boolean activeFlag;

	@EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
	
	@EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;

}
