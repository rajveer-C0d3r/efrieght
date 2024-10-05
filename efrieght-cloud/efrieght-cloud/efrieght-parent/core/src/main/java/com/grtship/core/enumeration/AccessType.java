package com.grtship.core.enumeration;

import lombok.Getter;

public enum AccessType {

	OPEN("Open"),
    RESTRICTED("Restricted");

	@Getter
	private String key;
	
	@Getter
	private String label;


	AccessType(String label) {
        this.label = label;
    }

    @Override
	public String toString() {
		return this.label;
	}
}
