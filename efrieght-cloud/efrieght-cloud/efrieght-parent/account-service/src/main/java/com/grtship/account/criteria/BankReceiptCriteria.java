package com.grtship.account.criteria;

import java.io.Serializable;

import lombok.Data;

@Data
public class BankReceiptCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long invoiceId;

	private String status;

	private Long externalEntityId;

	private String paymentType;
}
