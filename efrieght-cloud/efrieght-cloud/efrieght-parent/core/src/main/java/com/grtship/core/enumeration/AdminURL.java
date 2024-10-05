package com.grtship.core.enumeration;

import lombok.Getter;

public enum AdminURL {
	ROLE("Pending Approval"), COMPANY("Deactivated"), CLIENT("Approved");

	@Getter
	private String key;

	@Getter
	private String label;

	AdminURL(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public static String getName(String moduleName) {
		for (AdminURL adminURL : values()) {
			if (adminURL.label.equalsIgnoreCase(moduleName)) {
				return adminURL.name();
			}
		}
		return null;
	}
}
