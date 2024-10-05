package com.grtship.account.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
// FIXME change name of entity
public class BankReceiptLedger implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Column(name = "ledger_id")
	private Long ledgerId;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "posting_id")
	private Long postingId;

	@Column(name = "posting_date")
	private LocalDate postingDate;

}
