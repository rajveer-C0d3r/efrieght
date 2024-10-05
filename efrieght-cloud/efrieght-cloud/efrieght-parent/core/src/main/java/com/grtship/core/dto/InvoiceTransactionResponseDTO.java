package com.grtship.core.dto;

import java.time.LocalDate;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@EnableCustomAudit
public class InvoiceTransactionResponseDTO extends AbstractAuditingDTO{
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private String invoiceRefNo;
	
	private LocalDate invoiceDate;
	
	private String invoiceCurrency;
	
	private Double invoiceAmount;
	
	private Double adjustedAmount;
	
	private Double amount;
	
	private Double balanceAmount;
	
	private Boolean writeOff;
    
    private Double excessShortAmount;
}
