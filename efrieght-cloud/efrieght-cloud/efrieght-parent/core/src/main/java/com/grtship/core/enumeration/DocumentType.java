package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The DocumentType enumeration.
 */
public enum DocumentType {
    REGISTRATION("Registration"),
    OPERATIONAL("Operational");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	DocumentType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.key;
	}
}
