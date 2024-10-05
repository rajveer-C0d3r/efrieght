package com.grtship.core.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.grtship.core.annotation.EnableAuditLevel;
import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.AdjustmentType;
import com.grtship.core.enumeration.InstrumentType;
import com.grtship.core.enumeration.PartyType;
import com.grtship.core.enumeration.PaymentType;
import com.grtship.core.enumeration.TransactionStatus;
import com.grtship.core.enumeration.TransactionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class BankReceiptResponseDTO extends ClientAuditableEntityDTO {
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String voucherNo;

    private LocalDate voucherDate;
    
    private Integer version;

    private TransactionType transactionType;

    private InstrumentType instrumentType;

    private String instrumentNo;

    private LocalDate instrumentDate;

    private String bankName;

    private Double amountBaseCcy;

    private Double amountTxnCcy;

    private String txnCurrency;
    
    private String baseCurrency;

    private Double exchangeRate;

    private PartyType partyType;

    private Long externalEntityId;

    private Long entityBranchId;

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
    
    @EnableAuditLevel(idOnly = true)
    private Set<ShipmentReferenceDTO> shipmentReference = new HashSet<>();
    
    @EnableAuditLevel(idOnly = true)
    private Set<BankReceiptLedgerDTO> bankReceiptLedger = new HashSet<>();
    
    @EnableAuditLevel(idOnly = true)
    private Set<InvoiceTransactionResponseDTO> invoiceTransaction = new HashSet<>();
    
    @EnableAuditLevel(idOnly = true)
    private Set<PreviousBankReceiptResponseDTO> unadjustedAmountsDetails = new HashSet<>();
}
