package com.grtship.core.dto;

import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A DTO for the {@link com.grt.efreight.domain.EntityBranch} entity.
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class EntityBranchDTO extends EntityBranchRequestDTO{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Boolean activeFlag;
	
    @EnableAuditLevel(idOnly = true)
	private Set<DocumentDownloadDTO> entityBranchDocuments;

	// deactivation fields
    
    @EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
    
    @EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;
	
	// to update credit term customer and Vendor Id field
	private Long customerCreditTermId;
	private Long vendorCreditTermId;
	
	private Boolean submittedForApproval;

}
