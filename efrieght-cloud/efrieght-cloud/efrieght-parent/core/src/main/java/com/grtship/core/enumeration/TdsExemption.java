package com.grtship.core.enumeration;

import lombok.Getter;

public enum TdsExemption {
	NONE("None"),
	TOTAL("Total"),
	PARTIAL("Partial");
		
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	TdsExemption(String label) {
		this.label = label;
	}
}
