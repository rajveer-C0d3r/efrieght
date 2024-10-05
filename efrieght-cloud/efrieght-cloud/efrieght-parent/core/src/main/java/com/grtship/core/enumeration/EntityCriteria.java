package com.grtship.core.enumeration;

import lombok.Getter;

public enum EntityCriteria {
	DIRECT("Direct"),
    INDIRECT("Indirect");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	EntityCriteria(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
