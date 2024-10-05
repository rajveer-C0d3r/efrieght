package com.grtship.core.dto;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class GroupCreationDTO extends ClientAuditableEntityDTO{

	/**
	 * 
	 */
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private String code;

    @Size(max = 50, message = "Group name can be of max 50 character.")
    @NotEmpty(message = "Group name is mandatory.")
    @NotNull(message = "Group name is mandatory.")
    private String name;

    @NotNull(message = "Net Debit/Credit Balances for reporting is mandatory.")
    private Boolean netBalanceFlag = false;
    private Boolean submittedForApproval = false;
    private DomainStatus status;
    private Boolean subGroupFlag;
    private Long parentGroupId;
    private String treeId;
    private NatureOfGroup natureOfGroup;
    private Boolean affectsGrossProfitFlag;
    @EnableAuditLevel(idOnly = true)
    private Set<ObjectAliasDTO> aliases;
    private Boolean directIncomeFlag;
	private Boolean directExpenseFlag;
}
