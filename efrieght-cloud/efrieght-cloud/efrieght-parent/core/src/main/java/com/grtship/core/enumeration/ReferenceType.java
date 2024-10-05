package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The ReferenceType enumeration.
 */
public enum ReferenceType {
    SBILL_NO("SBill No."),
    CONTAINER_NO("Container No."),
    BL_NO("BL No.");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	ReferenceType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
