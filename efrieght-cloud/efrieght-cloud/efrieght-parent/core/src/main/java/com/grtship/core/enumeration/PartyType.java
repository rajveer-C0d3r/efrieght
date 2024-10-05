package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The PartyType enumeration.
 */
public enum PartyType {
    CUSTOMER("Customer"),
    VENDOR("Vendor");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	PartyType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
