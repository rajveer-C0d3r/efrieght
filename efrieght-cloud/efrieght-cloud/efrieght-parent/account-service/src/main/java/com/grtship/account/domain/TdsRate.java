package com.grtship.account.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.TdsRateStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A TdsRate.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_tds_rate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TdsRate extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Effective from is mandatory.")
	@Column(name = "effective_from", nullable = false)
	private LocalDate effectiveFrom;

	@Column(name = "effective_to")
	private LocalDate effectiveTo;

	@NotNull(message = "Tds is mandatory.")
	@Column(name = "tds_percentage", nullable = false)
	private Double tdsPercentage;

	@NotNull(message = "Basic is mandatory.")
	@Column(name = "basic_rate", nullable = false)
	private Double basicRate;

	@Column(name = "surcharge")
	private Double surcharge;

	@Column(name = "cess")
	private Double cess;

	@NotNull(message = "Status is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TdsRateStatus status;

	@Column(name = "reason")
	private String reason;

	@Column(name = "version")
	private Integer version;

	@Column(name = "tds_id")
	private Long tdsId;

	// jhipster-needle-entity-add-field - JHipster will add fields here

	public TdsRate effectiveFrom(LocalDate effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
		return this;
	}

	public TdsRate effectiveTo(LocalDate effectiveTo) {
		this.effectiveTo = effectiveTo;
		return this;
	}

	public TdsRate tds(Double tds) {
		this.tdsPercentage = tds;
		return this;
	}

	public TdsRate basic(Double basic) {
		this.basicRate = basic;
		return this;
	}

	public TdsRate surcharge(Double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public TdsRate cess(Double cess) {
		this.cess = cess;
		return this;
	}

	public TdsRate status(TdsRateStatus status) {
		this.status = status;
		return this;
	}

	public TdsRate reason(String reason) {
		this.reason = reason;
		return this;
	}

	public TdsRate version(Integer version) {
		this.version = version;
		return this;
	}

}
