package com.grtship.core.enumeration;

import lombok.Getter;

public enum GstType {

	GOODS("Goods"),
	SERVICE("Service");

	@Getter
	private String key;
	
	@Getter
	private String label;
	
	GstType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
