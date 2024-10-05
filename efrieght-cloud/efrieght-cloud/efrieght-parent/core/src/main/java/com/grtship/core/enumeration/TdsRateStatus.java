package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The TdsRateStatus enumeration.
 */
public enum TdsRateStatus {

	ACTIVE("Active"),
	INACTIVE("Inactive"),
	UPCOMING("Upcoming");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	TdsRateStatus(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
