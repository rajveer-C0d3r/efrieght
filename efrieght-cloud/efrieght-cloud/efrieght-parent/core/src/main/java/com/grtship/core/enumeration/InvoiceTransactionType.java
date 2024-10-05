package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The InvoiceTransactionType enumeration.
 */
public enum InvoiceTransactionType {
    WRITE_OFF("Write-Off"),
    ADJUSTMENT("Adjustment");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	InvoiceTransactionType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
