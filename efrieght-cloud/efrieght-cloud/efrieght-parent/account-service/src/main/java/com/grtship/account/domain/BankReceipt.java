package com.grtship.account.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.grtship.core.enumeration.AdjustmentType;
import com.grtship.core.enumeration.DomainStatus;
import com.grtship.core.enumeration.InstrumentType;
import com.grtship.core.enumeration.PartyType;
import com.grtship.core.enumeration.PaymentType;
import com.grtship.core.enumeration.TransactionStatus;
import com.grtship.core.enumeration.TransactionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A BankReceipt.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"shipmentReference", "bankReceiptLedger", "invoiceTransaction"})
@Entity
@Table(name = "acc_bank_receipt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BankReceipt extends ClientAuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 60)
	@NotNull(message = "Voucher No is mandatory.")
	@Column(name = "voucher_no", length = 60, nullable = false)
	private String voucherNo;

	@NotNull(message = "Voucher Date is mandatory.")
	@Column(name = "voucher_date", nullable = false)
	private LocalDate voucherDate = LocalDate.now();

	@Column(name = "version", nullable = false)
	private Integer version;

	@NotNull(message = "Transaction Type is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", length = 45, nullable = false)
	private TransactionType transactionType;

	@NotNull(message = "Instrument Type is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "instrument_type", length = 45, nullable = false)
	private InstrumentType instrumentType;

	@Size(max = 60)
	@Column(name = "instrument_no", length = 60)
	private String instrumentNo;

	@NotNull(message = "Instrument Date is mandatory.")
	@Column(name = "instrument_date", nullable = false)
	private LocalDate instrumentDate;

	@NotNull(message = "Received From Bank is mandatory.")
	@Column(name = "bank_name", nullable = false)
	private String bankName;

	@NotNull(message = "Amount Received is mandatory.")
	@Column(name = "amount_base_ccy", nullable = false)
	private Double amountBaseCcy;

	@Column(name = "amount_txn_ccy")
	private Double amountTxnCcy;

	@Column(name = "txn_currency_id")
	private Long txnCurrencyId;
	
	@Column(name = "base_currency_id")
	private Long baseCurrencyId;

	@Column(name = "exchange_rate")
	private Double exchangeRate;

	@NotNull(message = "Party Type is mandatory.")
	@Enumerated(EnumType.STRING)
	@Column(name = "party_type", length = 45, nullable = false)
	private PartyType partyType;

	@NotNull(message = "Party code is mandatoty.")
	@Column(name = "external_entity_id", nullable = false)
	private Long externalEntityId;

	@NotNull(message = "Party Branch is mandatory.")
	@Column(name = "entity_branch_id", nullable = false)
	private Long entityBranchId;

	@NotNull(message = "Party GST No is mandatory.")
	@Column(name = "tax_reference_no", nullable = false)
	private Long taxReferenceNo;

	@Column(name = "tds_deducted_flag")
	private Boolean tdsDeductedFlag;

	@Column(name = "tds_amount")
	private Double tdsAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_details", length = 60)
	private PaymentType paymentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "adjustment_type", length = 60)
	private AdjustmentType adjustmentType;

	@Column(name = "narration")
	private String narration;

	@Column(name = "adjusted_amount")
	private Double adjustedAmount;

	@Column(name = "unadjusted_amount")
	private Double unadjustedAmount;

	@Column(name = "main_module_id")
	private Long mainModuleId;

	@Column(name = "sub_module_id")
	private Long subModuleId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10)
	private TransactionStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", length = 20)
	private DomainStatus approvalStatus;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "bank_receipt_id", referencedColumnName = "id")
	private Set<ShipmentReference> shipmentReference = new HashSet<>();

	@ElementCollection
    @CollectionTable(name = "acc_bank_receipt_ledgers", joinColumns = @JoinColumn(name="bank_receipt_id"))
   	private Set<BankReceiptLedger> bankReceiptLedger = new HashSet<>();
	
	@OneToMany(mappedBy = "bankReceipt")
	private Set<InvoiceTransaction> invoiceTransaction = new HashSet<>();
}
