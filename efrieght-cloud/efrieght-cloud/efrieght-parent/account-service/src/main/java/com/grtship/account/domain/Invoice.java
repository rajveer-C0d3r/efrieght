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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.TransactionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Invoice.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Invoice extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 45)
	@Column(name = "ref_no", length = 45)
	private String refNo;

	@NotNull(message = "Version is mandatory.")
	@Column(name = "version", nullable = false)
	@Version
	private Integer version;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "currency_id")
	private Long currencyId;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "external_entity_id")
	private Long externalEntityId;

	@NotNull(message = "Invoice Status is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "invoice_status")
	private TransactionStatus invoiceStatus;

	@NotNull(message = "Adusted amount is mandatory.")
	@Column(name = "adjusted_amount")
	private Double adjustedAmount;

	@NotNull(message = "Balance amount is mandatory.")
	@Column(name = "balance_amount")
	private Double balanceAmount;

	@NotNull(message = "Narration is mandatory.")
	@Column(name = "narration")
	private String narration;

	@Column(name = "branch_id")
	private Long branchId;

	public Invoice refNo(String refNo) {
		this.refNo = refNo;
		return this;
	}

	public Invoice date(LocalDate date) {
		this.date = date;
		return this;
	}

	public Invoice currencyId(Long currencyId) {
		this.currencyId = currencyId;
		return this;
	}

	public Invoice amount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Invoice externalEntityId(Long externalEntityId) {
		this.externalEntityId = externalEntityId;
		return this;
	}

}
