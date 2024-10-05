package com.grtship.account.domain;

import java.time.LocalDate;
import java.util.Set;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.dto.ObjectAliasDTO;
import com.grtship.core.enumeration.DebitCredit;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Ledger.
 */
@Entity
@Table(name = "acc_ledger")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Ledger extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@NotBlank(message = "Ledger Code can't be null.")
	@Size(max = 6, message = "Max size of Code must be 6.")
	@Column(name = "code", nullable = false, length = 6)
	private String code;

	@NotBlank(message = "Ledger Name can't be null.")
	@Size(max = 50, message = "Max size of Name must be 50.")
	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@NotNull(message = "Cost Center Are Applicable can't be null.")
	@Column(name = "cost_center_applicable_flag", nullable = false)
	private Boolean costCenterApplicableFlag;

	@NotNull(message = "Opening Balance Date can't be null.")
	@Column(name = "opening_balance_date", nullable = false)
	private LocalDate openingBalanceDate;

	@NotNull(message = "Opening Balance Can't be null.")
	@Column(name = "opening_balance", nullable = false)
	private Double openingBalance;

	@Column(name = "active_flag")
	private Boolean activeFlag = Boolean.FALSE;

	// deactivate details
	@Embedded
	private DomainDeactivate deactivateDtls;

	@Embedded
	private DomainReactivate reactivateDtls;

	@Column(name = "group_root_id", length = 45)
	@Size(max = 45, message = "Maximum Size of GroupRootId must be 45.")
	private String groupRootId;

	@Enumerated(EnumType.STRING)
	@Column(name = "debit_credit", length = 10)
	@NotNull(message = "Debit Credit is required.")
	private DebitCredit debitCredit;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "Status is required.")
	@Column(name = "status", length = 45)
	private DomainStatus status;

	@ManyToOne
	@NotNull(message = "Group is required.")
	@JoinColumn(name = "group_id")
	private Group group;

	@Column(name = "submitted_for_approval")
	private Boolean submittedForApproval;

	@Transient
	private Set<ObjectAliasDTO> alias;

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public void setId(Long id) {
		this.id = id;
	}

	public Ledger group(Group group) {
		this.group = group;
		return this;
	}

	public Ledger code(String code) {
		this.code = code;
		return this;
	}

	public Ledger status(DomainStatus status) {
		this.status = status;
		return this;
	}

	public Ledger name(String name) {
		this.name = name;
		return this;
	}

	public Boolean isCostCenterApplicableFlag() {
		return costCenterApplicableFlag;
	}

	public Ledger costCenterApplicableFlag(Boolean costCenterApplicableFlag) {
		this.costCenterApplicableFlag = costCenterApplicableFlag;
		return this;
	}

	public Ledger openingBalanceDate(LocalDate openingBalanceDate) {
		this.openingBalanceDate = openingBalanceDate;
		return this;
	}

	public Ledger openingBalance(Double openingBalance) {
		this.openingBalance = openingBalance;
		return this;
	}

	public Ledger activeFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
		return this;
	}

	public Boolean isActiveFlag() {
		return activeFlag;
	}

	public Ledger groupRootId(String groupRootId) {
		this.groupRootId = groupRootId;
		return this;
	}

	public Ledger debitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
		return this;
	}

}
