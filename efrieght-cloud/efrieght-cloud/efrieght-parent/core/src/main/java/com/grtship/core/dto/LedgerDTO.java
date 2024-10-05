package com.grtship.core.dto;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.Ledger} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class LedgerDTO extends LedgerCreationDTO {

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Boolean activeFlag;

	private String groupRootName;
	
	private String groupName;
	
	private Boolean submittedForApproval;
	
	@EnableAuditLevel(level = 1)
	private DeactivationDTO deactivateDtls;
	
	@EnableAuditLevel(level = 1)
	private ReactivationDTO reactivateDtls;

}
