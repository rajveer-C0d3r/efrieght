package com.grtship.account.domain;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.CompanyType;
import com.grtship.core.enumeration.DomainStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Tds.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_tds")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tds extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Code is mandatory.")
	@Column(name = "code", nullable = false)
	private String code;

	@NotNull(message = "Description is mandatory.")
	@Column(name = "description", nullable = false)
	private String description;

	@NotNull(message = "Section Code is mandatory.")
	@Column(name = "section_code", nullable = false)
	private String sectionCode;

	@NotNull(message = "Ledger is Mandatory.")
	@OneToOne
	@JoinColumn(name = "ledger_id", referencedColumnName = "id")
	private Ledger ledger;

	@NotNull(message = "Company Type is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "company_type", nullable = false)
	private CompanyType companyType;

	@NotNull(message = "Tds Type is mandatory.")
	@ManyToOne
	@JoinColumn(name = "tds_type_id", referencedColumnName = "id")
	private TdsType tdsType;

	@Column(name = "branch_id")
	private Long branchId;

	@Column(name = "active_flag")
	private Boolean activeFlag;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private DomainStatus status;

	@Column(name = "deactivation_wef_date")
	private Instant deactivationWefDate;

	@Column(name = "deactivated_date")
	private Instant deactivatedDate;

	@Column(name = "reactivation_wef_date")
	private Instant reactivationWefDate;

	@Column(name = "reactivated_date")
	private Instant reactivatedDate;

	@Column(name = "deactivation_reason")
	private String deactivationReason;

	@Column(name = "reactivation_reason")
	private String reactivationReason;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "tds_id", referencedColumnName = "id")
	private Set<TdsRate> tdsRates = new HashSet<>();

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public Tds code(String code) {
		this.code = code;
		return this;
	}

	public Tds description(String description) {
		this.description = description;
		return this;
	}

	public Tds sectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
		return this;
	}

	public Tds ledger(Ledger ledger) {
		this.ledger = ledger;
		return this;
	}

	public Tds activeFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
		return this;
	}

	public DomainStatus getStatus() {
		return status;
	}

	public Tds deactivationWefDate(Instant deactivationWefDate) {
		this.deactivationWefDate = deactivationWefDate;
		return this;
	}

	public Tds deactivatedDate(Instant deactivatedDate) {
		this.deactivatedDate = deactivatedDate;
		return this;
	}

	public Tds reactivationWefDate(Instant reactivationWefDate) {
		this.reactivationWefDate = reactivationWefDate;
		return this;
	}

	public Tds reactivatedDate(Instant reactivatedDate) {
		this.reactivatedDate = reactivatedDate;
		return this;
	}

	public Tds deactivationReason(String deactivationReason) {
		this.deactivationReason = deactivationReason;
		return this;
	}

	public Tds reactivationReason(String reactivationReason) {
		this.reactivationReason = reactivationReason;
		return this;
	}

}
