package com.grtship.core.enumeration;

import lombok.Getter;

/**
 * The AccountType enumeration.
 */
public enum AccountType { 
    SAVING_ACCOUNT("Saving Account"),
    CURRENT_ACCOUNT("Current Account"),
    RECURRING_ACCOUNT("Recurring Account"),
    FIXED_DEPOSIT_ACCOUNT("Fixed Deposit Account"),
    FOREIGN_CURRENCY_ACCOUNT("Foreign Currency Account");

	@Getter
	private String key;
	
	@Getter
	private String label;


    AccountType(String label) {
        this.label = label;
    }

    @Override
	public String toString() {
		return this.label;
	}
}
