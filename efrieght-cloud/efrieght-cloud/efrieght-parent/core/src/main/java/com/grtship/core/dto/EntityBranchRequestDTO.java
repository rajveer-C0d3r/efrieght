package com.grtship.core.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.EntityLevel;

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
public class EntityBranchRequestDTO extends ClientAuditableEntityDTO{
	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String code;

	@NotBlank(message = "Branch name can't be null.")
	private String name;

	@NotNull(message = "External Entity Id can't be null.")
	private Long externalEntityId;
	
	@EnableAuditLevel(level = 1)
	private AddressDTO address;
	
    private Boolean defaultBranchFlag;

    @EnableAuditLevel
	private Set<String> cellNumbers;

	private Date sezWEFDate;

	private Date sezValidUptoDate;

	private Boolean sez;
	
	private DomainStatus status;
	
	//credit terms fields
	private Boolean customerFlag = Boolean.FALSE;
	private DomainStatus customerApprovalStatus;
	private EntityLevel customerEntityLevel;
	private Integer customerCreditDays;
	private Double customerCreditAmount;
	
	private Boolean vendorFlag = Boolean.FALSE;
	private DomainStatus vendorApprovalStatus;
    private EntityLevel vendorEntityLevel;
	private Integer vendorCreditDays;
	private Double vendorCreditAmount;
	
	@EnableAuditLevel(idOnly = true)
	private List<EntityBranchTaxDTO> taxDetialsDto;//for gst details
	@EnableAuditLevel(idOnly = true)
	private List<BranchContactDTO> contactDetailsDto;
	
	@JsonIgnore
	@EnableAuditLevel(idOnly = true)
	private List<CreditTermsDTO> creditTermsList;
}