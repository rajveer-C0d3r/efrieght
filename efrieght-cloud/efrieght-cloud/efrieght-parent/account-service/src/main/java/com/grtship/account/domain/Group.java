package com.grtship.account.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.NatureOfGroup;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Group.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Group extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Group code is mandatory.")
	@Size(max = 6, message = "Max size of Code must be 6.")
	@Column(name = "code", nullable = false, length = 6)
	private String code;

	@Size(max = 50, message = "Group name can be of max 50 character.")
	@NotBlank(message = "Group name is mandatory.")
	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "sub_group_flag")
	private Boolean subGroupFlag;

	@ManyToOne
	@JoinColumn(name = "parent_group_id")
	private Group parentGroup;

	@Size(max = 150, message = "Max size of Tree id must be 150.")
	@Column(name = "tree_id", length = 150)
	private String treeId;

	@NotNull(message = "Net Debit/Credit Balances for reporting is mandatory.")
	@Column(name = "net_balance_flag", nullable = false)
	private Boolean netBalanceFlag = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "nature_of_group", length = 15)
	private NatureOfGroup natureOfGroup;

	@Column(name = "affects_gross_profit_flag")
	private Boolean affectsGrossProfitFlag;

	@Column(name = "submitted_for_approval")
	private Boolean submittedForApproval = false;

	@Column(name = "active_flag")
	private Boolean activeFlag = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20)
	private DomainStatus status;

	@Embedded
	private DomainDeactivate deactivate;

	@Embedded
	private DomainReactivate reactivate;

	@Column(name = "direct_income_flag")
	private Boolean directIncomeFlag;

	@Column(name = "direct_expense_flag")
	private Boolean directExpenseFlag;

	public Group reactivate(DomainReactivate reactivate) {
		this.reactivate = reactivate;
		return this;
	}

	public Group deactivate(DomainDeactivate deactivate) {
		this.deactivate = deactivate;
		return this;
	}

	public Group code(String code) {
		this.code = code;
		return this;
	}

	public Group name(String name) {
		this.name = name;
		return this;
	}

	public Group subGroupFlag(Boolean subGroupFlag) {
		this.subGroupFlag = subGroupFlag;
		return this;
	}

	public void setParentGroup(Group parentGroup) {
		if (parentGroup != null && parentGroup.getId() != null)
			this.parentGroup = parentGroup;
	}

	public Group netBalanceFlag(Boolean netBalanceFlag) {
		this.netBalanceFlag = netBalanceFlag;
		return this;
	}

	public Group natureOfGroup(NatureOfGroup natureOfGroup) {
		this.natureOfGroup = natureOfGroup;
		return this;
	}

	public Group affectsGrossProfitFlag(Boolean affectsGrossProfitFlag) {
		this.affectsGrossProfitFlag = affectsGrossProfitFlag;
		return this;
	}

	public Group activeFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
		return this;
	}

}
