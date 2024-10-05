package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;
import com.grtship.core.enumeration.InstrumentType;
import com.grtship.core.enumeration.PaymentType;

import lombok.Data;

@Data
@EnableCustomAudit
public class PreviousBankReceiptResponseDTO implements Serializable {

	@IgnoreAuditField
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String voucherNo;

    private LocalDate voucherDate;
    
    private InstrumentType instrumentType;

    private String instrumentNo;

    private LocalDate instrumentDate;
    
    private String bankName;

    private Double invoiceAdjustedAmountBaseCcy;

    private Double invoiceAdjustedAmountTxnCcy;

    private String currency;

    private Double exchangeRate;
    
    private Double unadjustedAmount;
    private PaymentType invoiceType;
}
