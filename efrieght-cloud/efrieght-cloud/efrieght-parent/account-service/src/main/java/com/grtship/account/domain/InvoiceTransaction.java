package com.grtship.account.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.InvoiceTransactionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A InvoiceTransaction.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "acc_invoice_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InvoiceTransaction extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "invoice_id", nullable = true)
	private Invoice invoice;

	@NotNull(message = "Invoice Transaction type is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false)
	private InvoiceTransactionType transactionType;

	@ManyToOne
	@JoinColumn(name = "bank_receipt_id")
	private BankReceipt bankReceipt;

	@NotNull(message = "Amount is mandatory.")
	@Column(name = "transaction_amount", nullable = false)
	private Double transactionAmount;

	@NotNull(message = "Transaction Date is mandatory.")
	@Column(name = "transaction_date", nullable = false)
	private LocalDate transactionDate;

	@Column(name = "authorised_by")
	private Long authorisedBy;

	@Column(name = "posting_id")
	private Long postingId;

	public void setInvoice(Invoice invoice) {
		if (invoice != null && invoice.getId() != null)
			this.invoice = invoice;
	}

	public void setBankReceipt(BankReceipt bankReceipt) {
		if (bankReceipt != null && bankReceipt.getId() != null)
			this.bankReceipt = bankReceipt;
	}
}
