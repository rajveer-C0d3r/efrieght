package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The EntityGstType enumeration.
 */
public enum EntityGstType {
    REGISTERED("Registered"), 
    UNREGISTERED("UnRegistered");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	EntityGstType(String label) {
		this.label = label;
	}
}
