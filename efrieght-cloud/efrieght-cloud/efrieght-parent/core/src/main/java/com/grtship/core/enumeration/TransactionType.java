package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The TransactionType enumeration.
 */
public enum TransactionType {
    DIRECT_INCOME("Direct Income"),
    INDIRECT_INCOME("Indirect Income");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	TransactionType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
