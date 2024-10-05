package com.grtship.core.enumeration;

import lombok.Getter;

public enum ClientURL {
	GROUP("accounts/group/edit/"), 
	COMPANY("administration/company/edit/"),
	ROLE("user-management/role/edit/"),
	LEDGER("accounts/ledger/edit/");

	@Getter
	private String key;

	@Getter
	private String label;

	ClientURL(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public static String getName(String moduleName) {
		for (ClientURL clientURL : values()) {
			if (clientURL.label.equalsIgnoreCase(moduleName)) {
				return clientURL.name();
			}
		}
		return null;
	}
}
