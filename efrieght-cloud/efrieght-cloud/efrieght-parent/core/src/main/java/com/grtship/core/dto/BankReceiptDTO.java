package com.grtship.core.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
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
 * A DTO for the {@link com.grt.efreight.account.domain.BankReceipt} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class BankReceiptDTO extends ClientAuditableEntityDTO {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

	private String voucherNo;

    @NotNull(message = "Voucher Date is mandatory.")
    private LocalDate voucherDate;
    
    private Integer version;

    @NotNull(message = "Transaction Type is mandatory.")
    private TransactionType transactionType;

    @NotNull(message ="Instrument Type is mandatory.")
    private InstrumentType instrumentType;

    private String instrumentNo;

    @NotNull(message = "Instrument Date is mandatory.")
    private LocalDate instrumentDate;

    @NotNull(message = "Received From Bank is mandatory.")
    private String bankName;

    @NotNull(message = "Amount Received is mandatory.")
    private Double amountBaseCcy;

    private Double amountTxnCcy;

    private Long txnCurrencyId;
    
    private Long baseCurrencyId;

    private Double exchangeRate;

    @NotNull(message = "Party Type is mandatory.")
    private PartyType partyType;

    @NotNull(message = "Party code is mandatoty.")
    private Long externalEntityId;

    @NotNull(message = "Party Branch is mandatory.")
    private Long entityBranchId;

    @NotNull(message = "Party GST No is mandatory.")
    private Long taxReferenceNo;

    private Boolean tdsDeductedFlag;

    private Double tdsAmount;

    private PaymentType paymentType;

    private AdjustmentType adjustmentType;

    private String narration;

    private Double adjustedAmount;

    private Double unadjustedAmount;

    private Long mainModuleId;

    private Long subModuleId;
    
    private TransactionStatus status;
    
    private DomainStatus approvalStatus;
    
    private Set<ShipmentReferenceDTO> shipmentReference = new HashSet<>();
    
    private Set<BankReceiptLedgerDTO> bankReceiptLedger = new HashSet<>();
    
    private Set<InvoiceTransactionDTO> invoiceTransaction = new HashSet<>();
}
