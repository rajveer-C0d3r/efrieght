package com.grtship.core.dto;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DebitCredit;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class LedgerCreationDTO extends ClientAuditableEntityDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String code;

	@NotNull(message = "Ledger Name can't be null.")
	private String name;

	@NotNull(message = "Cost Center Are Applicable can't be null.")
	private Boolean costCenterApplicableFlag;

	@NotNull(message = "Opening Balance Date can't be null.")
	private LocalDate openingBalanceDate;

	@NotNull(message = "Opening Balance Can't be null.")
	private Double openingBalance;
	
	private String groupRootId;
	
	private DebitCredit debitCredit;

	private DomainStatus status;
	
	private Long groupId;
	
	@EnableAuditLevel(idOnly = true)
	private Set<ObjectAliasDTO> alias;

}
