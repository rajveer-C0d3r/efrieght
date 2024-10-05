package com.grtship.core.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.grtship.core.annotation.EnableCustomAudit;
import com.grtship.core.annotation.IgnoreAuditField;

import lombok.Data;

@Data
@EnableCustomAudit
public class BankReceiptLedgerDTO implements Serializable { // FIXME change name of dto
	@IgnoreAuditField
	private static final long serialVersionUID = 1L;

	private Long ledgerId;
	
	private Double amount;
	
	private Long postingId;
	
	private LocalDate postingDate;
	
	private String ledgerCode;
	
	private String ledgerName;
	
	private String groupName;
}
