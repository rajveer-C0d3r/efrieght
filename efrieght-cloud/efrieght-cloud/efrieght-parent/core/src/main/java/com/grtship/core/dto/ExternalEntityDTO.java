package com.grtship.core.dto;

import java.util.List;
import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.domain.ExternalEntity} entity.
 */
@Data
@EqualsAndHashCode(callSuper=true)
@EnableCustomAudit
public class ExternalEntityDTO extends ExternalEntityRequestDTO {
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private String currencyName;

	private Boolean activeFlag = Boolean.FALSE;
	private Boolean submittedForApproval;

	//credit terms fields
	private DomainStatus customerApprovalStatus;
	private DomainStatus vendorApprovalStatus;
	private Long customerCreditTermId;
	private Long vendorCreditTermId;

	// deactivation-reactivation fields
	@EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
	@EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;

	@EnableAuditLevel(idOnly = true)
	private Set<DocumentDownloadDTO> entityDocuments;
	@EnableAuditLevel(idOnly = true)
	private List<EntityBranchDTO> branchDetailsDto;
}
