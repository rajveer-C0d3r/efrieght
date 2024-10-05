package com.grtship.core.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.InvoiceTransactionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the {@link com.grt.efreight.account.domain.InvoiceTransaction} entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class InvoiceTransactionDTO extends AbstractAuditingDTO{
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long invoiceId;
    
    @NotNull(message = "Invoice Version is mandatory.")
    private Integer invoiceVersion;

    private InvoiceTransactionType transactionType;

    private Long bankReceiptId;

    @NotNull(message = "Amount is mandatory.")
    private Double transactionAmount;

    private LocalDate transactionDate;

    private Long authorisedBy;

    private Long postingId;
    
    private Boolean writeOff;
    
    private Double excessShortAmount;
    
}
