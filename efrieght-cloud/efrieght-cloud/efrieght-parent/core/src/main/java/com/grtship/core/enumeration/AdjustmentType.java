package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The AdjustmentType enumeration.
 */
public enum AdjustmentType {
    FIFO("FIFO"),
    AS_PER_CUSTOMER_PAYMENT_ADVICE("As Per Customer Payment Advice");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	AdjustmentType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
