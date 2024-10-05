package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The Domicile enumeration.
 */
public enum Domicile {
    DOMESTIC("Domestic Entity"),
    FOREIGN("Foreign Entity");
	
	@Getter
	private String key;
	
	@Getter
	private String label;
	
	Domicile(String label) {
		this.label = label;
	}
}
