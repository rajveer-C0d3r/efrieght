package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The BankReceiptStatus enumeration.
 */
public enum TransactionStatus {
	OPEN("Open"),
    CLOSED("Closed"),
    CANCELLED("Cancelled");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	TransactionStatus(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
