package com.grtship.core.dto;

import java.time.Instant;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.Tds} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class TdsDTO extends ClientAuditableEntityDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull(message = "Code is mandatory.")
    private String code;

    @NotNull(message = "Description is mandatory.")
    private String description;

    @NotNull(message = "Section Code is mandatory.")
    private String sectionCode;

    @NotNull(message = "Ledger is Mandatory.")
    private Long ledgerId;
    
    private String ledgerName;
    
    @NotNull(message = "Company Type is mandatory.")
    private CompanyType companyType;
    
    @NotNull(message = "Tds Type is mandatory.")
    private Long tdsTypeId;
    
    private String tdsTypeName;
    
    private Long branchId;

    private Long groupId;
    
    private String groupName;

    private Boolean activeFlag;
    
    private DomainStatus status;

    private Instant deactivationWefDate;

    private Instant deactivatedDate;

    private Instant reactivationWefDate;

    private Instant reactivatedDate;

    private String deactivationReason;

    private String reactivationReason;

    @EnableAuditLevel(idOnly = true)
    private Set<TdsRateDTO> tdsRates;
    
}
