package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The DebitCredit enumeration.
 */
public enum DebitCredit {
    DEBIT("Debit"),
    CREDIT("Credit");

	@Getter
    private final String label;


    DebitCredit(String label) {
        this.label = label;
    }

    @Override
	public String toString() {
		return this.label;
	}
}
