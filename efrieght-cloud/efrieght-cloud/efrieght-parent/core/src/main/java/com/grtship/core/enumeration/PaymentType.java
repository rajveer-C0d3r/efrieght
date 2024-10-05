package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The PaymentDetails enumeration.
 */
public enum PaymentType {
    PAYMENT_AGAINST_BILL("Payment Against Bill"),
    ADVANCE_PAYMENT("Advance Payment"),
    ON_ACCOUNT("On Account");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	PaymentType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
