package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The EntityLevel enumeration.
 */
public enum EntityLevel {
	BRANCH("Branch"),
	COMPANY("Company"),
    GLOBAL("Global");
    
    @Getter
	private String key;
	
	@Getter
	private String label;
	
	EntityLevel(String label) {
		this.label = label;
	}
}
