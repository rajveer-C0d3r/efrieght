package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The GstVatType enumeration.
 */

public enum GstVatType {
	NONE("None"),
    GST("GST"),
    VAT("VAT"),
    SALES_TAX("Sales Tax");
    
    @Getter
	private String key;
	
	@Getter
	private String label;
	
	GstVatType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
