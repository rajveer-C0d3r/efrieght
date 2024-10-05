package com.grtship.account.criteria;

import java.io.Serializable;
import java.util.Collection;

import lombok.Data;

@Data
public class InvoiceCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long externalEntityId;

	private String invoiceStatus;

	private Long currencyId;

	private Collection<Long> ids;
}
